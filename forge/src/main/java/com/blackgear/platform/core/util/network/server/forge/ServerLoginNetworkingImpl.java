package com.blackgear.platform.core.util.network.server.forge;

import com.blackgear.platform.core.mixin.forge.core.networking.access.ServerLoginPacketListenerImplAccessor;
import com.blackgear.platform.core.util.network.server.ServerLoginNetworking.LoginQueryResponseHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class ServerLoginNetworkingImpl {
    public static boolean registerGlobalReceiver(ResourceLocation channel, LoginQueryResponseHandler handler) {
        return ServerNetworking.LOGIN.registerGlobalReceiver(channel, handler);
    }
    
    @Nullable
    public static LoginQueryResponseHandler unregisterGlobalReceiver(ResourceLocation channel) {
        return ServerNetworking.LOGIN.unregisterGlobalReceiver(channel);
    }
    
    public static Set<ResourceLocation> getGlobalReceivers() {
        return ServerNetworking.LOGIN.getChannels();
    }
    
    public static boolean registerReceiver(ServerLoginPacketListenerImpl listener, ResourceLocation channel, LoginQueryResponseHandler handler) {
        Objects.requireNonNull(listener, "listener cannot be null");
        
        return ServerNetworking.getAddon(listener).registerChannel(channel, handler);
    }
    
    @Nullable
    public static LoginQueryResponseHandler unregisterReceiver(ServerLoginPacketListenerImpl listener, ResourceLocation channel) {
        Objects.requireNonNull(listener, "listener cannot be null");
        
        return ServerNetworking.getAddon(listener).unregisterChannel(channel);
    }
    
    public static MinecraftServer getServer(ServerLoginPacketListenerImpl listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        
        return ((ServerLoginPacketListenerImplAccessor) listener).getServer();
    }
}
