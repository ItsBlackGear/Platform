package com.blackgear.platform.fabric;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.events.fabric.ServerLifecycle;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class PlatformFabric implements ModInitializer {
    static MinecraftServer server;
    
    @Override
    public void onInitialize() {
        Platform.bootstrap();
        
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            PlatformFabric.server = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            PlatformFabric.server = null;
        });
        
        ServerLifecycle.bootstrap();
    }
    
    @Nullable
    public static MinecraftServer getServer() {
        return server;
    }
}