package com.blackgear.platform.core.util.network.client.forge;

import com.blackgear.platform.core.util.network.PacketByteBufs;
import com.blackgear.platform.core.util.network.PacketSender;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class AbstractChanneledNetworkAddon<H> extends AbstractNetworkAddon<H> implements PacketSender {
    protected final Connection connection;
    protected final GlobalReceiverRegistry<H> receiver;
    protected final Set<ResourceLocation> sendableChannels;
    protected final Set<ResourceLocation> sendableChannelsView;
    
    protected AbstractChanneledNetworkAddon(GlobalReceiverRegistry<H> receiver, Connection connection) {
        this(receiver, connection, new HashSet<>());
    }
    
    protected AbstractChanneledNetworkAddon(GlobalReceiverRegistry<H> receiver, Connection connection, Set<ResourceLocation> sendableChannels) {
        super(receiver);
        this.connection = connection;
        this.receiver = receiver;
        this.sendableChannels = sendableChannels;
        this.sendableChannelsView = Collections.unmodifiableSet(sendableChannels);
    }
    
    public abstract void lateInit();
    
    protected void registerPendingChannels(ChannelInfoHolder holder) {
        final Collection<ResourceLocation> pending = holder.getPendingChannelNames();
        
        if (!pending.isEmpty()) {
            this.register(new ArrayList<>(pending));
            pending.clear();
        }
    }
    
    protected boolean handle(ResourceLocation channel, FriendlyByteBuf buffer) {
        LOGGER.debug("Handling inbound packet from channel with name \"{}\"", channel);
        
        if (Networking.REGISTER_CHANNEL.equals(channel)) {
            this.receiveRegistration(true, PacketByteBufs.slice(buffer));
            return true;
        }
        
        if (Networking.UNREGISTER_CHANNEL.equals(channel)) {
            this.receiveRegistration(false, PacketByteBufs.slice(buffer));
            return true;
        }
        
        @Nullable H handler = this.getHandler(channel);
        
        if (handler == null) {
            return false;
        }
        
        FriendlyByteBuf slice = PacketByteBufs.slice(buffer);
        
        try {
            this.receive(handler, slice);
        } catch (Throwable exception) {
            LOGGER.error("An exception occurred while handling an inbound packet from channel with name \"{}\"", channel, exception);
            throw exception;
        }
        
        return true;
    }
    
    protected abstract void receive(H handler, FriendlyByteBuf buffer);
    
    protected void sendInitialChannelRegistrationPacket() {
        final FriendlyByteBuf buffer = this.createRegistrationPacket(this.getSendableChannels());
        
        if (buffer != null) {
            this.sendPacket(Networking.REGISTER_CHANNEL, buffer);
        }
    }
    
    @Nullable
    protected FriendlyByteBuf createRegistrationPacket(Collection<ResourceLocation> channels) {
        if (channels.isEmpty()) {
            return null;
        }
        
        final FriendlyByteBuf buffer = PacketByteBufs.create();
        boolean first = true;
        
        for (ResourceLocation channel : channels) {
            if (first) {
                first = false;
            } else {
                buffer.writeByte(0);
            }
            
            buffer.writeBytes(channel.toString().getBytes(StandardCharsets.US_ASCII));
        }
        
        return buffer;
    }
    
    protected void receiveRegistration(boolean register, FriendlyByteBuf buffer) {
        List<ResourceLocation> ids = new ArrayList<>();
        StringBuilder active = new StringBuilder();
        
        while (buffer.isReadable()) {
            byte b = buffer.readByte();
            
            if (b != 0) {
                active.append(AsciiString.b2c(b));
            } else {
                this.addId(ids, active);
                active = new StringBuilder();
            }
        }
        
        this.addId(ids, active);
        this.schedule(register ? () -> this.register(ids) : () -> this.unregister(ids));
    }
    
    void register(List<ResourceLocation> ids) {
        this.sendableChannels.addAll(ids);
        this.invokeRegisterEvent(ids);
    }
    
    void unregister(List<ResourceLocation> ids) {
        ids.forEach(this.sendableChannels::remove);
        this.invokeUnregisterEvent(ids);
    }
    
    @Override
    public void sendPacket(Packet<?> packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        
        this.connection.send(packet);
    }
    
    @Override
    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        sendPacket(packet, GenericFutureListenerHolder.create(callback));
    }
    
    @Override
    public void sendPacket(Packet<?> packet, PacketSendListener callback) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        
        this.connection.send(packet, callback);
    }
    
    protected abstract void schedule(Runnable task);
    
    protected abstract void invokeRegisterEvent(List<ResourceLocation> channels);
    
    protected abstract void invokeUnregisterEvent(List<ResourceLocation> channels);
    
    private void addId(List<ResourceLocation> channels, StringBuilder builder) {
        String literal = builder.toString();
        
        try {
            channels.add(new ResourceLocation(literal));
        } catch (IllegalArgumentException exception) {
            LOGGER.warn("Received invalid channel identifier \"{}\" from connection {}", literal, this.connection);
        }
    }
    
    public Set<ResourceLocation> getSendableChannels() {
        return this.sendableChannels;
    }
}