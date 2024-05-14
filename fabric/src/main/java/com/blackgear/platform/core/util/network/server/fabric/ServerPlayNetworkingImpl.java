package com.blackgear.platform.core.util.network.server.fabric;

import com.blackgear.platform.core.util.network.FabricPacketSender;
import com.blackgear.platform.core.util.network.PacketSender;
import com.blackgear.platform.core.util.network.PlatformPacketSender;
import com.blackgear.platform.core.util.network.server.ServerPlayNetworking.PlayChannelHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ServerPlayNetworkingImpl {
    public static boolean registerGlobalReceiver(ResourceLocation channel, PlayChannelHandler handler) {
        return ServerPlayNetworking.registerGlobalReceiver(channel, (server, player, handler1, buffer, sender) -> handler.receive(server, player, handler1, buffer, new PlatformPacketSender(sender)));
    }
    
    @Nullable
    public static PlayChannelHandler unregisterGlobalReceiver(ResourceLocation channel) {
        ServerPlayNetworking.PlayChannelHandler handler = ServerPlayNetworking.unregisterGlobalReceiver(channel);
        if (handler != null) {
            return (server, player, listener, buffer, sender) -> handler.receive(server, player, listener, buffer, new FabricPacketSender(sender));
        }
        
        return null;
    }
    
    public static Set<ResourceLocation> getGlobalReceivers() {
        return ServerPlayNetworking.getGlobalReceivers();
    }
    
    public static boolean registerReceiver(ServerGamePacketListenerImpl listener, ResourceLocation channel, PlayChannelHandler handler) {
        return ServerPlayNetworking.registerReceiver(listener, channel, (server, player, handler1, buffer, sender) -> handler.receive(server, player, handler1, buffer, new PlatformPacketSender(sender)));
    }
    
    @Nullable
    public static PlayChannelHandler unregisterReceiver(ServerGamePacketListenerImpl listener, ResourceLocation channel) {
        ServerPlayNetworking.PlayChannelHandler handler = ServerPlayNetworking.unregisterReceiver(listener, channel);
        if (handler != null) {
            return (server, player, listener1, buffer, sender) -> handler.receive(server, player, listener1, buffer, new FabricPacketSender(sender));
        }
        
        return null;
    }
    
    public static Set<ResourceLocation> getReceived(ServerPlayer player) {
        return ServerPlayNetworking.getReceived(player);
    }
    
    public static Set<ResourceLocation> getReceived(ServerGamePacketListenerImpl listener) {
        return ServerPlayNetworking.getReceived(listener);
    }
    
    public static Set<ResourceLocation> getSendable(ServerPlayer player) {
        return ServerPlayNetworking.getSendable(player);
    }
    
    public static Set<ResourceLocation> getSendable(ServerGamePacketListenerImpl listener) {
        return ServerPlayNetworking.getSendable(listener);
    }
    
    public static boolean canSend(ServerPlayer player, ResourceLocation channel) {
        return ServerPlayNetworking.canSend(player, channel);
    }
    
    public static boolean canSend(ServerGamePacketListenerImpl listener, ResourceLocation channel) {
        return ServerPlayNetworking.canSend(listener, channel);
    }
    
    public static Packet<?> createS2CPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        return ServerPlayNetworking.createS2CPacket(channel, buffer);
    }
    
    public static PacketSender getSender(ServerPlayer player) {
        return new PlatformPacketSender(ServerPlayNetworking.getSender(player));
    }
    
    public static PacketSender getSender(ServerGamePacketListenerImpl listener) {
        return new PlatformPacketSender(ServerPlayNetworking.getSender(listener));
    }
    
    public static void send(ServerPlayer player, ResourceLocation channel, FriendlyByteBuf buffer) {
        ServerPlayNetworking.send(player, channel, buffer);
    }
    
    public static MinecraftServer getServer(ServerGamePacketListenerImpl listener) {
        return ServerPlayNetworking.getServer(listener);
    }
}