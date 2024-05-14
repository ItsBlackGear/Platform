package com.blackgear.platform.core.util.network.server;

import com.blackgear.platform.core.util.network.PacketSender;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ServerPlayNetworking {
    @ExpectPlatform
    public static boolean registerGlobalReceiver(ResourceLocation channel, PlayChannelHandler handler) {
        throw new AssertionError();
    }
    
    @ExpectPlatform @Nullable
    public static PlayChannelHandler unregisterGlobalReceiver(ResourceLocation channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Set<ResourceLocation> getGlobalReceivers() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static boolean registerReceiver(ServerGamePacketListenerImpl listener, ResourceLocation channel, PlayChannelHandler handler) {
        throw new AssertionError();
    }
    
    @ExpectPlatform @Nullable
    public static PlayChannelHandler unregisterReceiver(ServerGamePacketListenerImpl listener, ResourceLocation channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Set<ResourceLocation> getReceived(ServerPlayer player) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Set<ResourceLocation> getReceived(ServerGamePacketListenerImpl listener) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Set<ResourceLocation> getSendable(ServerPlayer player) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Set<ResourceLocation> getSendable(ServerGamePacketListenerImpl listener) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static boolean canSend(ServerPlayer player, ResourceLocation channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static boolean canSend(ServerGamePacketListenerImpl listener, ResourceLocation channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Packet<?> createS2CPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static PacketSender getSender(ServerPlayer player) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static PacketSender getSender(ServerGamePacketListenerImpl listener) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void send(ServerPlayer player, ResourceLocation channel, FriendlyByteBuf buffer) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static MinecraftServer getServer(ServerGamePacketListenerImpl listener) {
        throw new AssertionError();
    }
    
    @FunctionalInterface
    public interface PlayChannelHandler {
        void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, FriendlyByteBuf buffer, PacketSender sender);
    }
}