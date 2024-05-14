package com.blackgear.platform.core.util.network.server.forge;

import com.blackgear.platform.core.util.network.PacketSender;
import com.blackgear.platform.core.util.network.server.ServerNetworking;
import com.blackgear.platform.core.util.network.server.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class ServerPlayNetworkingImpl {
    public static boolean registerGlobalReceiver(ResourceLocation channel, PlayChannelHandler handler) {
        return ServerNetworking.PLAY.registerGlobalReceiver(channel, handler);
    }
    
    @Nullable
    public static PlayChannelHandler unregisterGlobalReceiver(ResourceLocation channel) {
        return ServerNetworking.PLAY.unregisterGlobalReceiver(channel);
    }
    
    public static Set<ResourceLocation> getGlobalReceivers() {
        return ServerNetworking.PLAY.getChannels();
    }
    
    public static boolean registerReceiver(ServerGamePacketListenerImpl listener, ResourceLocation channel, PlayChannelHandler handler) {
        Objects.requireNonNull(listener, "listener cannot be null");
        
        return ServerNetworking.getAddon(listener).registerChannel(channel, handler);
    }
    
    @Nullable
    public static PlayChannelHandler unregisterReceiver(ServerGamePacketListenerImpl listener, ResourceLocation channel) {
        Objects.requireNonNull(listener, "listener cannot be null");
        
        return ServerNetworking.getAddon(listener).unregisterChannel(channel);
    }
    
    public static Set<ResourceLocation> getReceived(ServerPlayer player) {
        Objects.requireNonNull(player, "player cannot be null");
        
        return getReceived(player.connection);
    }
    
    public static Set<ResourceLocation> getReceived(ServerGamePacketListenerImpl listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        
        return ServerNetworking.getAddon(listener).getSendableChannels();
    }
    
    public static Set<ResourceLocation> getSendable(ServerPlayer player) {
        Objects.requireNonNull(player, "player cannot be null");
        
        return getSendable(player.connection);
    }
    
    public static Set<ResourceLocation> getSendable(ServerGamePacketListenerImpl listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        
        return ServerNetworking.getAddon(listener).getSendableChannels();
    }
    
    public static boolean canSend(ServerPlayer player, ResourceLocation channel) {
        Objects.requireNonNull(player, "player cannot be null");
        
        return canSend(player.connection, channel);
    }
    
    public static boolean canSend(ServerGamePacketListenerImpl listener, ResourceLocation channel) {
        Objects.requireNonNull(listener, "listener cannot be null");
        Objects.requireNonNull(channel, "channel cannot be null");
        
        return ServerNetworking.getAddon(listener).getSendableChannels().contains(channel);
    }
    
    public static Packet<?> createS2CPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        Objects.requireNonNull(channel, "channel cannot be null");
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return ServerNetworking.createPlayC2SPacket(channel, buffer);
    }
    
    public static PacketSender getSender(ServerPlayer player) {
        Objects.requireNonNull(player, "player cannot be null");
        
        return getSender(player.connection);
    }
    
    public static PacketSender getSender(ServerGamePacketListenerImpl listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        
        return ServerNetworking.getAddon(listener);
    }
    
    public static void send(ServerPlayer player, ResourceLocation channel, FriendlyByteBuf buffer) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(channel, "channel cannot be null");
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        player.connection.send(createS2CPacket(channel, buffer));
    }
    
    public static MinecraftServer getServer(ServerGamePacketListenerImpl listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        
        return listener.player.server;
    }
}