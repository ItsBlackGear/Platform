package com.blackgear.platform.core.util.network.client;

import com.blackgear.platform.core.util.network.AbstractChanneledNetworkAddon;
import com.blackgear.platform.core.util.network.ChannelInfoHolder;
import com.blackgear.platform.core.util.network.Networking;
import com.blackgear.platform.core.util.network.client.ClientPlayNetworking.PlayChannelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientPlayNetworkAddon extends AbstractChanneledNetworkAddon<PlayChannelHandler> {
    private final ClientPacketListener listener;
    private final Minecraft client;
    private boolean sentInitialRegisterPacket;
    
    protected ClientPlayNetworkAddon(ClientPacketListener listener, Minecraft client) {
        super(ClientNetworking.PLAY, listener.getConnection());
        this.listener = listener;
        this.client = client;
        
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
        
        ClientPlayConnectionEvents.INIT.invoker().onPlayInit(this.listener, this.client);
    }
    
    public void onServerReady() {
        ClientPlayConnectionEvents.JOIN.invoker().onPlayReady(this.listener, this, this.client);
        
        // The client cannot send any packets, including `minecraft:register` until after GameJoinS2CPacket is received.
        this.sendInitialChannelRegistrationPacket();
        this.sentInitialRegisterPacket = true;
    }
    
    public boolean handle(ClientboundCustomPayloadPacket packet) {
        // Do not handle the packet on game thread
        if (this.client.isSameThread()) {
            return false;
        }
        
        FriendlyByteBuf buffer = packet.getData();
        
        try {
            return this.handle(packet.getIdentifier(), buffer);
        } finally {
            buffer.release();
        }
    }
    
    @Override
    protected void receive(PlayChannelHandler handler, FriendlyByteBuf buffer) {
        handler.receive(this.client, this.listener, buffer, this);
    }
    
    @Override
    protected void schedule(Runnable task) {
        Minecraft.getInstance().execute(task);
    }
    
    @Override
    public Packet<?> createPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        return ClientPlayNetworking.createC2SPacket(channel, buffer);
    }
    
    @Override
    protected void invokeRegisterEvent(List<ResourceLocation> channels) {
        C2SPlayChannelEvents.REGISTER.invoker().onChannelRegister(this.listener, this, this.client, channels);
    }
    
    @Override
    protected void invokeUnregisterEvent(List<ResourceLocation> channels) {
        C2SPlayChannelEvents.UNREGISTER.invoker().onChannelUnregister(this.listener, this, this.client, channels);
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
        ClientPlayConnectionEvents.DISCONNECT.invoker().onPlayDisconnect(this.listener, this.client);
        this.receiver.endSession(this);
    }
    
    @Override
    protected boolean isReservedChannel(ResourceLocation channel) {
        return Networking.isReservedPlayChannel(channel);
    }
}