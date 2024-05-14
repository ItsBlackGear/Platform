package com.blackgear.platform.core.util.network.client.fabric;

import com.blackgear.platform.core.util.network.FabricPacketSender;
import com.blackgear.platform.core.util.network.PacketSender;
import com.blackgear.platform.core.util.network.PlatformPacketSender;
import com.blackgear.platform.core.util.network.client.ClientPlayNetworking.PlayChannelHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ClientPlayNetworkingImpl {
    public static boolean registerGlobalReceiver(ResourceLocation channel, PlayChannelHandler handler) {
        return ClientPlayNetworking.registerGlobalReceiver(channel, (client, handler1, buffer, sender) -> handler.receive(client, handler1, buffer, new PlatformPacketSender(sender)));
    }
    
    @Nullable
    public static PlayChannelHandler unregisterGlobalReceiver(ResourceLocation channel) {
        ClientPlayNetworking.PlayChannelHandler handler = ClientPlayNetworking.unregisterGlobalReceiver(channel);
        if (handler != null) {
            return (client, handler1, buffer, sender) -> handler.receive(client, handler1, buffer, new FabricPacketSender(sender));
        }
        
        return null;
    }
    
    public static Set<ResourceLocation> getGlobalReceivers() {
        return ClientPlayNetworking.getGlobalReceivers();
    }
    
    public static boolean registerReceiver(ResourceLocation channel, PlayChannelHandler handler) {
        return ClientPlayNetworking.registerReceiver(channel, (client, handler1, buffer, sender) -> handler.receive(client, handler1, buffer, new PlatformPacketSender(sender)));
    }
    
    @Nullable
    public static PlayChannelHandler unregisterReceiver(ResourceLocation channel) {
        ClientPlayNetworking.PlayChannelHandler handler = ClientPlayNetworking.unregisterReceiver(channel);
        if (handler != null) {
            return (client, handler1, buffer, sender) -> handler.receive(client, handler1, buffer, new FabricPacketSender(sender));
        }
        
        return null;
    }
    
    public static Set<ResourceLocation> getReceived() {
        return ClientPlayNetworking.getReceived();
    }
    
    public static Set<ResourceLocation> getSendable() {
        return ClientPlayNetworking.getSendable();
    }
    
    public static boolean canSend(ResourceLocation channel) {
        return ClientPlayNetworking.canSend(channel);
    }
    
    public static Packet<?> createC2SPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        return ClientPlayNetworking.createC2SPacket(channel, buffer);
    }
    
    public static PacketSender getSender() {
        return new PlatformPacketSender(ClientPlayNetworking.getSender());
    }
    
    public static void send(ResourceLocation channel, FriendlyByteBuf buffer) {
        ClientPlayNetworking.send(channel, buffer);
    }
}