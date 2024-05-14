package com.blackgear.platform.core.util.network.client.forge;

import com.blackgear.platform.core.util.network.PacketSender;
import com.blackgear.platform.core.util.network.client.ClientNetworking;
import com.blackgear.platform.core.util.network.client.ClientPlayNetworkAddon;
import com.blackgear.platform.core.util.network.client.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class ClientPlayNetworkingImpl {
    public static boolean registerGlobalReceiver(ResourceLocation channel, ClientPlayNetworking.PlayChannelHandler handler) {
        return ClientNetworking.PLAY.registerGlobalReceiver(channel, handler);
    }
    
    @Nullable
    public static ClientPlayNetworking.PlayChannelHandler unregisterGlobalReceiver(ResourceLocation channel) {
        return ClientNetworking.PLAY.unregisterGlobalReceiver(channel);
    }
    
    public static Set<ResourceLocation> getGlobalReceivers() {
        return ClientNetworking.PLAY.getChannels();
    }
    
    public static boolean registerReceiver(ResourceLocation channel, ClientPlayNetworking.PlayChannelHandler handler) {
        final ClientPlayNetworkAddon addon = ClientNetworking.getClientPlayAddon();
        
        if (addon != null) {
            return addon.registerChannel(channel, handler);
        }
        
        throw new IllegalStateException("Cannot register receiver while not in game!");
    }
    
    @Nullable
    public static ClientPlayNetworking.PlayChannelHandler unregisterReceiver(ResourceLocation channel) throws IllegalStateException {
        final ClientPlayNetworkAddon addon = ClientNetworking.getClientPlayAddon();
        
        if (addon != null) {
            return addon.unregisterChannel(channel);
        }
        
        throw new IllegalStateException("Cannot unregister receiver while not in game!");
    }
    
    public static Set<ResourceLocation> getReceived() throws IllegalStateException {
        final ClientPlayNetworkAddon addon = ClientNetworking.getClientPlayAddon();
        
        if (addon != null) {
            return addon.getSendableChannels();
        }
        
        throw new IllegalStateException("Cannot get a list of channels the client can receive packets on while not in game!");
    }
    
    public static Set<ResourceLocation> getSendable() throws IllegalStateException {
        final ClientPlayNetworkAddon addon = ClientNetworking.getClientPlayAddon();
        
        if (addon != null) {
            return addon.getSendableChannels();
        }
        
        throw new IllegalStateException("Cannot get a list of channels the server can receive packets on while not in game!");
    }
    
    public static boolean canSend(ResourceLocation channel) throws IllegalArgumentException {
        // You cant send without a client player, so this is fine
        if (Minecraft.getInstance().getConnection() != null) {
            return ClientNetworking.getAddon(Minecraft.getInstance().getConnection()).getSendableChannels().contains(channel);
        }
        
        return false;
    }
    
    public static Packet<?> createC2SPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        Objects.requireNonNull(channel, "channel cannot be null");
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return ClientNetworking.createPlayC2SPacket(channel, buffer);
    }
    
    public static PacketSender getSender() throws IllegalStateException {
        // You cant send without a client player, so this is fine
        if (Minecraft.getInstance().getConnection() != null) {
            return ClientNetworking.getAddon(Minecraft.getInstance().getConnection());
        }
        
        throw new IllegalStateException("Cannot get packet sender when not in game!");
    }
    
    public static void send(ResourceLocation channel, FriendlyByteBuf buffer) throws IllegalStateException {
        // You cant send without a client player, so this is fine
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(createC2SPacket(channel, buffer));
            return;
        }
        
        throw new IllegalStateException("Cannot send packets when not in game!");
    }
}