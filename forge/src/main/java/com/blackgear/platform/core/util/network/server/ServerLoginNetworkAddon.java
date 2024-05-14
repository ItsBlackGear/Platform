package com.blackgear.platform.core.util.network.server;

import com.blackgear.platform.core.mixin.core.networking.access.ClientboundCustomQueryPacketAccessor;
import com.blackgear.platform.core.mixin.core.networking.access.ServerLoginPacketListenerImplAccessor;
import com.blackgear.platform.core.mixin.core.networking.access.ServerboundCustomQueryPacketAccessor;
import com.blackgear.platform.core.util.network.AbstractNetworkAddon;
import com.blackgear.platform.core.util.network.PacketByteBufs;
import com.blackgear.platform.core.util.network.PacketSender;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ServerLoginNetworkAddon extends AbstractNetworkAddon<ServerLoginNetworking.LoginQueryResponseHandler> implements PacketSender {
    private final Connection connection;
    private final ServerLoginPacketListenerImpl handler;
    private final MinecraftServer server;
    private final QueryIdFactory queryIdFactory;
    private final Collection<java.util.concurrent.Future<?>> waits = new ConcurrentLinkedQueue<>();
    private final Map<Integer, ResourceLocation> channels = new ConcurrentHashMap<>();
    private boolean firstQueryTick = true;
    
    public ServerLoginNetworkAddon(ServerLoginPacketListenerImpl handler) {
        super(ServerNetworking.LOGIN);
        this.connection = handler.connection;
        this.handler = handler;
        this.server = ((ServerLoginPacketListenerImplAccessor) handler).getServer();
        this.queryIdFactory = QueryIdFactory.create();
        
        ServerLoginConnectionEvents.INIT.invoker().onLoginInit(handler, this.server);
        this.receiver.startSession(this);
    }
    
    public boolean queryTick() {
        if (this.firstQueryTick) {
            // Send the compression packet now so clients receive compressed login queries
            this.sendCompressionPacket();
            
            // Register global receivers.
            for (Map.Entry<ResourceLocation, ServerLoginNetworking.LoginQueryResponseHandler> entry : ServerNetworking.LOGIN.getHandlers().entrySet()) {
                ServerLoginNetworking.registerReceiver(this.handler, entry.getKey(), entry.getValue());
            }
            
            ServerLoginConnectionEvents.QUERY_START.invoker().onLoginStart(this.handler, this.server, this, this.waits::add);
            this.firstQueryTick = false;
        }
        
        AtomicReference<Throwable> error = new AtomicReference<>();
        this.waits.removeIf(future -> {
            if (!future.isDone()) {
                return false;
            }
            
            try {
                future.get();
            } catch (ExecutionException ex) {
                Throwable caught = ex.getCause();
                error.getAndUpdate(oldEx -> {
                    if (oldEx == null) {
                        return caught;
                    }
                    
                    oldEx.addSuppressed(caught);
                    return oldEx;
                });
            } catch (InterruptedException | CancellationException ignored) {
                // ignore
            }
            
            return true;
        });
        
        return this.channels.isEmpty() && this.waits.isEmpty();
    }
    
    private void sendCompressionPacket() {
        // Compression is not needed for local transport
        if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
            this.connection.send(new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()), future ->
                this.connection.setupCompression(this.server.getCompressionThreshold())
            );
        }
    }
    
    public boolean handle(ServerboundCustomQueryPacket packet) {
        ServerboundCustomQueryPacketAccessor access = (ServerboundCustomQueryPacketAccessor) packet;
        return handle(access.getTransactionId(), access.getData());
    }
    
    private boolean handle(int transaction, @Nullable FriendlyByteBuf buffer) {
        LOGGER.debug("Handling inbound login query with transaction {}", transaction);
        ResourceLocation channel = this.channels.remove(transaction);
        
        if (channel == null) {
            LOGGER.warn("Query transaction {} was received but no query has been associated in {}!", transaction, this.connection);
            return false;
        }
        
        boolean understood = buffer != null;
        @Nullable ServerLoginNetworking.LoginQueryResponseHandler handler = ServerNetworking.LOGIN.getHandler(channel);
        
        if (handler == null) {
            return false;
        }
        
        FriendlyByteBuf result = understood ? PacketByteBufs.slice(buffer) : PacketByteBufs.empty();
        
        try {
            handler.receive(this.server, this.handler, understood, result, this.waits::add, this);
        } catch (Throwable ex) {
            LOGGER.error("Encountered exception while handling in channel \"{}\"", channel, ex);
            throw ex;
        }
        
        return true;
    }
    
    @Override
    public Packet<?> createPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        int transaction = this.queryIdFactory.nextId();
        ClientboundCustomQueryPacket result = new ClientboundCustomQueryPacket();
        // The constructor for creating a non-empty response was removed by proguard
        ClientboundCustomQueryPacketAccessor access = (ClientboundCustomQueryPacketAccessor) result;
        access.setTransactionId(transaction);
        access.setIdentifier(channel);
        access.setData(buffer);
        return result;
    }
    
    @Override
    public void sendPacket(Packet<?> packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        
        this.connection.send(packet);
    }
    
    @Override
    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        
        this.connection.send(packet, callback);
    }
    
    public void registerOutgoingPacket(ClientboundCustomQueryPacket packet) {
        ClientboundCustomQueryPacketAccessor access = (ClientboundCustomQueryPacketAccessor) packet;
        this.channels.put(access.getTransactionId(), access.getIdentifier());
    }
    
    @Override
    protected void handleRegistration(ResourceLocation channel) {}
    
    @Override
    protected void handleUnregistration(ResourceLocation channel) {}
    
    @Override
    protected void invokeDisconnectEvent() {
        ServerLoginConnectionEvents.DISCONNECT.invoker().onLoginDisconnect(this.handler, this.server);
        this.receiver.endSession(this);
    }
    
    public void handlePlayTransition() {
        this.receiver.endSession(this);
    }
    
    @Override
    protected boolean isReservedChannel(ResourceLocation channel) {
        return false;
    }
    
    public interface QueryIdFactory {
        static QueryIdFactory create() {
            return new QueryIdFactory() {
                private final AtomicInteger currentId = new AtomicInteger();
                
                @Override
                public int nextId() {
                    return this.currentId.getAndIncrement();
                }
            };
        }
        
        int nextId();
    }
}