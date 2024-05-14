package com.blackgear.platform.core.util.network.client;

import com.blackgear.platform.core.util.network.PacketSender;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class ClientPlayNetworking {
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
    public static boolean registerReceiver(ResourceLocation channel, PlayChannelHandler handler) {
        throw new AssertionError();
    }
    
    @ExpectPlatform @Nullable
    public static PlayChannelHandler unregisterReceiver(ResourceLocation channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Set<ResourceLocation> getReceived() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Set<ResourceLocation> getSendable() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static boolean canSend(ResourceLocation channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Packet<?> createC2SPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static PacketSender getSender() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void send(ResourceLocation channel, FriendlyByteBuf buffer) {
        throw new AssertionError();
    }
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface PlayChannelHandler {
        void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender sender);
    }
}