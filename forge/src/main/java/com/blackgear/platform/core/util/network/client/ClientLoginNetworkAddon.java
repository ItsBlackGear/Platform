package com.blackgear.platform.core.util.network.client;

import com.blackgear.platform.core.mixin.core.networking.access.ClientboundCustomQueryPacketAccessor;
import com.blackgear.platform.core.util.network.AbstractNetworkAddon;
import com.blackgear.platform.core.util.network.FutureListeners;
import com.blackgear.platform.core.util.network.PacketByteBufs;
import com.blackgear.platform.core.util.network.client.ClientLoginNetworking.LoginQueryRequestHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public class ClientLoginNetworkAddon extends AbstractNetworkAddon<LoginQueryRequestHandler> {
    private final ClientHandshakePacketListenerImpl listener;
    private final Minecraft client;
    private boolean firstResponse = true;
    
    public ClientLoginNetworkAddon(ClientHandshakePacketListenerImpl listener, Minecraft client) {
        super(ClientNetworking.LOGIN);
        this.listener = listener;
        this.client = client;
        
        ClientLoginConnectionEvents.INIT.invoker().onLoginStart(this.listener, this.client);
        this.receiver.startSession(this);
    }
    
    public boolean handlePacket(ClientboundCustomQueryPacket packet) {
        ClientboundCustomQueryPacketAccessor access = (ClientboundCustomQueryPacketAccessor) packet;
        return handlePacket(packet.getTransactionId(), access.getIdentifier(), access.getData());
    }
    
    private boolean handlePacket(int transaction, ResourceLocation channel, FriendlyByteBuf buffer) {
        LOGGER.debug("Handling inbound login response with transaction {} and channel {}", transaction, channel);
        
        if (this.firstResponse) {
            for (Map.Entry<ResourceLocation, LoginQueryRequestHandler> entry : ClientNetworking.LOGIN.getHandlers().entrySet()) {
                ClientLoginNetworking.registerReceiver(entry.getKey(), entry.getValue());
            }
            
            ClientLoginConnectionEvents.QUERY_START.invoker().onLoginQueryStart(this.listener, this.client);
            this.firstResponse = false;
        }
        
        @Nullable LoginQueryRequestHandler handler = this.getHandler(channel);
        
        if (handler == null) {
            return false;
        }
        
        FriendlyByteBuf slice = PacketByteBufs.slice(buffer);
        List<GenericFutureListener<? extends Future<? super Void>>> listeners = new ArrayList<>();
        
        try {
            CompletableFuture<@Nullable FriendlyByteBuf> future = handler.receive(this.client, this.listener, slice, listeners::add);
            future.thenAccept(result -> {
                ServerboundCustomQueryPacket packet = new ServerboundCustomQueryPacket(transaction, result);
                GenericFutureListener<? extends Future<? super Void>> listener = null;
                
                for (GenericFutureListener<? extends Future<? super Void>> entry : listeners) {
                    listener = FutureListeners.union(listener, entry);
                }
                
                this.listener.getConnection().send(packet, listener);
            });
        } catch (Throwable exception) {
            LOGGER.error("Encountered exception while handling in channel with name \"{}\"", channel, exception);
            throw exception;
        }
        
        return true;
    }
    
    @Override
    protected void handleRegistration(ResourceLocation channel) {}
    
    @Override
    protected void handleUnregistration(ResourceLocation channel) {}
    
    @Override
    protected void invokeDisconnectEvent() {
        ClientLoginConnectionEvents.DISCONNECT.invoker().onLoginDisconnect(this.listener, this.client);
        this.receiver.endSession(this);
    }
    
    public void handlePlayTransition() {
        this.receiver.endSession(this);
    }
    
    @Override
    protected boolean isReservedChannel(ResourceLocation channel) {
        return false;
    }
}