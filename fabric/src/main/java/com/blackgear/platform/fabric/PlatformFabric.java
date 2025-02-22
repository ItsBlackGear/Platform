package com.blackgear.platform.fabric;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ServerLifecycle;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class PlatformFabric implements ModInitializer {
    static MinecraftServer server;
    
    @Override
    public void onInitialize() {
        Platform.bootstrap();
        registerServerLifecycle();

        if (Environment.isClientSide()) {
            FabricClientEvents.bootstrap();
        }

        FabricCommonEvents.bootstrap();
        
        ServerLifecycle.bootstrap();
    }

    private static void registerServerLifecycle() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> PlatformFabric.server = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> PlatformFabric.server = null);
    }

    @Nullable
    public static MinecraftServer getServer() {
        return server;
    }
}