package com.blackgear.platform.fabric;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.v2.emissive.fabric.BlockSpriteListener;
import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.fabric.ServerLifecycle;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class PlatformFabric implements ModInitializer {
    private static MinecraftServer server;
    public static RegistryAccess registryAccess;

    @Override
    public void onInitialize() {
        Platform.bootstrap();
        registerServerLifecycleEvents();

        if (Environment.isClientSide()) {
            FabricClientEvents.bootstrap();
            BlockSpriteListener.init();
        }

        FabricCommonEvents.bootstrap();
        ServerLifecycle.bootstrap();
    }

    private void registerServerLifecycleEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            PlatformFabric.server = server;
            registryAccess = server.registryAccess();
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> PlatformFabric.server = null);
    }

    @Nullable
    public static MinecraftServer getServer() {
        return server;
    }
}