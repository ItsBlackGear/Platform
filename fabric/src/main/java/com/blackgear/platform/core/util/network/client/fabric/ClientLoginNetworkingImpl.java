package com.blackgear.platform.core.util.network.client.fabric;

import com.blackgear.platform.core.util.network.client.ClientLoginNetworking.LoginQueryRequestHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ClientLoginNetworkingImpl {
    public static boolean registerGlobalReceiver(ResourceLocation channel, LoginQueryRequestHandler handler) {
        return ClientLoginNetworking.registerGlobalReceiver(channel, handler::receive);
    }
    
    @Nullable
    public static LoginQueryRequestHandler unregisterGlobalReceiver(ResourceLocation channel) {
        ClientLoginNetworking.LoginQueryRequestHandler handler = ClientLoginNetworking.unregisterGlobalReceiver(channel);
        if (handler != null) {
            return handler::receive;
        }
        
        return null;
    }
    
    public static Set<ResourceLocation> getGlobalReceivers() {
        return ClientLoginNetworking.getGlobalReceivers();
    }
    
    public static boolean registerReceiver(ResourceLocation channel, LoginQueryRequestHandler handler) {
        return ClientLoginNetworking.registerReceiver(channel, handler::receive);
    }
    
    @Nullable
    public static LoginQueryRequestHandler unregisterReceiver(ResourceLocation channel) {
        ClientLoginNetworking.LoginQueryRequestHandler handler = ClientLoginNetworking.unregisterReceiver(channel);
        if (handler != null) {
            return handler::receive;
        }
        
        return null;
    }
}