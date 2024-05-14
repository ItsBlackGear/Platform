package com.blackgear.platform.core.util.network.client.forge;

import com.blackgear.platform.core.util.network.client.ClientLoginNetworking.LoginQueryRequestHandler;
import com.blackgear.platform.core.util.network.client.ClientNetworking;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ClientLoginNetworkingImpl {
    public static boolean registerGlobalReceiver(ResourceLocation channel, LoginQueryRequestHandler handler) {
        return ClientNetworking.LOGIN.registerGlobalReceiver(channel, handler);
    }
    
    @Nullable
    public static LoginQueryRequestHandler unregisterGlobalReceiver(ResourceLocation channel) {
        return ClientNetworking.LOGIN.unregisterGlobalReceiver(channel);
    }
    
    public static Set<ResourceLocation> getGlobalReceivers() {
        return ClientNetworking.LOGIN.getChannels();
    }
    
    public static boolean registerReceiver(ResourceLocation channel, LoginQueryRequestHandler handler) throws IllegalStateException {
        final Connection connection = ClientNetworking.getLoginConnection();
        
        if (connection != null) {
            final PacketListener listener = connection.getPacketListener();
            
            if (listener instanceof ClientHandshakePacketListenerImpl) {
                return ClientNetworking.getAddon(((ClientHandshakePacketListenerImpl) listener)).registerChannel(channel, handler);
            }
        }
        
        throw new IllegalStateException("Cannot register receiver while client is not logging in!");
    }
    
    public static @Nullable LoginQueryRequestHandler unregisterReceiver(ResourceLocation channel) throws IllegalStateException {
        final Connection connection = ClientNetworking.getLoginConnection();
        
        if (connection != null) {
            final PacketListener packetListener = connection.getPacketListener();
            
            if (packetListener instanceof ClientHandshakePacketListenerImpl) {
                return ClientNetworking.getAddon(((ClientHandshakePacketListenerImpl) packetListener)).unregisterChannel(channel);
            }
        }
        
        throw new IllegalStateException("Cannot unregister receiver while client is not logging in!");
    }
}