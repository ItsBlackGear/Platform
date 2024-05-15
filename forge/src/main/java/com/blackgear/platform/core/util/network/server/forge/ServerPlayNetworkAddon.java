package com.blackgear.platform.core.util.network.server.forge;

import com.blackgear.platform.core.mixin.core.networking.access.ServerGamePacketListenerImplAccessor;
import com.blackgear.platform.core.util.network.client.forge.AbstractChanneledNetworkAddon;
import com.blackgear.platform.core.util.network.client.forge.ChannelInfoHolder;
import com.blackgear.platform.core.util.network.client.forge.Networking;
import com.blackgear.platform.core.util.network.server.S2CPlayChannelEvents;
import com.blackgear.platform.core.util.network.server.ServerPlayConnectionEvents;
import com.blackgear.platform.core.util.network.server.ServerPlayNetworking;
import com.blackgear.platform.core.util.network.server.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ServerPlayNetworkAddon extends AbstractChanneledNetworkAddon<PlayChannelHandler> {
    private final ServerGamePacketListenerImpl listener;
    private final MinecraftServer server;
    private boolean sentInitialRegisterPacket;
    
    public ServerPlayNetworkAddon(ServerGamePacketListenerImpl listener, MinecraftServer server) {
        super(ServerNetworking.PLAY, ((ServerGamePacketListenerImplAccessor) listener).getConnection());
        this.listener = listener;
        this.server = server;
        
        // Must register pending channels via lateinit
        this.registerPendingChannels((ChannelInfoHolder) this.connection);
        
        // Register global receivers and attach to session
        this.receiver.startSession(this);
    }
    
    @Override
    public void lateInit() {
        for (Map.Entry<ResourceLocation, PlayChannelHandler> entry : this.receiver.getHandlers().entrySet()) {
            this.registerChannel(entry.getKey(), entry.getValue());
        }
        
        ServerPlayConnectionEvents.INIT.invoker().onPlayInit(this.listener, this.server);
    }
    
    public void onClientReady() {
        ServerPlayConnectionEvents.JOIN.invoker().onPlayReady(this.listener, this, this.server);
        
        this.sendInitialChannelRegistrationPacket();
        this.sentInitialRegisterPacket = true;
    }
    
    public boolean handle(ServerboundCustomPayloadPacket packet) {
        // Do not handle the packet on game thread
        if (this.server.isSameThread()) {
            return false;
        }
        
        return this.handle(packet.getIdentifier(), packet.getData());
    }
    
    @Override
    protected void receive(PlayChannelHandler handler, FriendlyByteBuf buffer) {
        handler.receive(this.server, this.listener.player, this.listener, buffer, this);
    }
    
    @Override
    protected void schedule(Runnable task) {
        this.listener.player.server.execute(task);
    }
    
    @Override
    public Packet<?> createPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        return ServerPlayNetworking.createS2CPacket(channel, buffer);
    }
    
    @Override
    protected void invokeRegisterEvent(List<ResourceLocation> channels) {
        S2CPlayChannelEvents.REGISTER.invoker().onChannelRegister(this.listener, this, this.server, channels);
    }
    
    @Override
    protected void invokeUnregisterEvent(List<ResourceLocation> channels) {
        S2CPlayChannelEvents.UNREGISTER.invoker().onChannelUnregister(this.listener, this, this.server, channels);
    }
    
    @Override
    protected void handleRegistration(ResourceLocation channel) {
        // If we can already send packets, immediately send the register packet for this channel
        if (this.sentInitialRegisterPacket) {
            final FriendlyByteBuf buffer = this.createRegistrationPacket(Collections.singleton(channel));
            
            if (buffer != null) {
                this.sendPacket(Networking.REGISTER_CHANNEL, buffer);
            }
        }
    }
    
    @Override
    protected void handleUnregistration(ResourceLocation channel) {
        // If we can already send packets, immediately send the unregister packet for this channel
        if (this.sentInitialRegisterPacket) {
            final FriendlyByteBuf buffer = this.createRegistrationPacket(Collections.singleton(channel));
            
            if (buffer != null) {
                this.sendPacket(Networking.UNREGISTER_CHANNEL, buffer);
            }
        }
    }
    
    @Override
    protected void invokeDisconnectEvent() {
        ServerPlayConnectionEvents.DISCONNECT.invoker().onPlayDisconnect(this.listener, this.server);
        this.receiver.endSession(this);
    }
    
    @Override
    protected boolean isReservedChannel(ResourceLocation channel) {
        return Networking.isReservedPlayChannel(channel);
    }
}