package com.blackgear.platform.core.util.network.server.fabric;

import com.blackgear.platform.core.util.network.FabricPacketSender;
import com.blackgear.platform.core.util.network.PlatformPacketSender;
import com.blackgear.platform.core.util.network.server.ServerLoginNetworking.LoginQueryResponseHandler;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ServerLoginNetworkingImpl {
    public static boolean registerGlobalReceiver(ResourceLocation channel, LoginQueryResponseHandler handler) {
        return ServerLoginNetworking.registerGlobalReceiver(channel, (server, handler1, understood, buffer, synchronizer, sender) -> handler.receive(server, handler1, understood, buffer, synchronizer::waitFor, new PlatformPacketSender(sender)));
    }
    
    @Nullable
    public static LoginQueryResponseHandler unregisterGlobalReceiver(ResourceLocation channel) {
        ServerLoginNetworking.LoginQueryResponseHandler handler = ServerLoginNetworking.unregisterGlobalReceiver(channel);
        if (handler != null) {
            return (server, handler1, understood, buffer, synchronizer, sender) -> {
                handler.receive(server, handler1, understood, buffer, synchronizer::waitFor, new FabricPacketSender(sender));
            };
        }
        
        return null;
    }
    
    public static Set<ResourceLocation> getGlobalReceivers() {
        return ServerLoginNetworking.getGlobalReceivers();
    }
    
    public static boolean registerReceiver(ServerLoginPacketListenerImpl listener, ResourceLocation channel, LoginQueryResponseHandler handler) {
        return ServerLoginNetworking.registerReceiver(listener, channel, (server, handler1, understood, buffer, synchronizer, sender) -> handler.receive(server, handler1, understood, buffer, synchronizer::waitFor, new PlatformPacketSender(sender)));
    }
    
    @Nullable
    public static LoginQueryResponseHandler unregisterReceiver(ServerLoginPacketListenerImpl listener, ResourceLocation channel) {
        ServerLoginNetworking.LoginQueryResponseHandler handler = ServerLoginNetworking.unregisterReceiver(listener, channel);
        if (handler != null) {
            return (server, handler1, understood, buffer, synchronizer, sender) -> {
                handler.receive(server, handler1, understood, buffer, synchronizer::waitFor, new FabricPacketSender(sender));
            };
        }
        
        return null;
    }
    
    public static MinecraftServer getServer(ServerLoginPacketListenerImpl listener) {
        return ServerLoginNetworking.getServer(listener);
    }
}