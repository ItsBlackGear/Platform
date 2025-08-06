package com.blackgear.platform.core.util.config;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ServerLifecycleEvents;
import com.blackgear.platform.core.mixin.access.LevelResourceAccessor;
import com.blackgear.platform.core.networking.ServerListenerEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    static final LevelResource SERVERCONFIG = LevelResourceAccessor.createLevelResource("serverconfig");

    private static Path getServerConfigPath(MinecraftServer server) {
        Path config = server.getWorldPath(SERVERCONFIG);
        if (!Files.isDirectory(config)) {
            try {
                Files.createDirectories(config);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to create " + config, exception);
            }
        }

        return config;
    }

    public static void bootstrap() {
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, Environment.getConfigDir());
        if (Environment.isClientSide()) {
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, Environment.getConfigDir());
        }

        ServerLifecycleEvents.STARTING.register(server -> ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server)));
        ServerLifecycleEvents.STOPPING.register(server -> ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server)));

        ServerListenerEvents.JOIN.register((handler, server) -> ConfigTracker.INSTANCE.syncConfigs(Environment.isClientSide()));
    }
}