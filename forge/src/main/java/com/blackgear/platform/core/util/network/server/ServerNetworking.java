package com.blackgear.platform.core.util.network.server;

import com.blackgear.platform.core.util.network.GlobalReceiverRegistry;
import com.blackgear.platform.core.util.network.NetworkHandlerExtensions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

public class ServerNetworking {
    public static final GlobalReceiverRegistry<ServerLoginNetworking.LoginQueryResponseHandler> LOGIN = new GlobalReceiverRegistry<>();
    public static final GlobalReceiverRegistry<ServerPlayNetworking.PlayChannelHandler> PLAY = new GlobalReceiverRegistry<>();
    
    public static ServerPlayNetworkAddon getAddon(ServerGamePacketListenerImpl listener) {
        return (ServerPlayNetworkAddon) ((NetworkHandlerExtensions) listener).getAddon();
    }
    
    public static ServerLoginNetworkAddon getAddon(ServerLoginPacketListenerImpl listener) {
        return (ServerLoginNetworkAddon) ((NetworkHandlerExtensions) listener).getAddon();
    }
    
    public static Packet<?> createPlayC2SPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        return new ClientboundCustomPayloadPacket(channel, buffer);
    }
}