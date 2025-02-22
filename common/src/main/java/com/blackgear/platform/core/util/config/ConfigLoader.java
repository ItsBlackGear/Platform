package com.blackgear.platform.core.util.config;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.events.ServerLifecycleEvents;
import com.blackgear.platform.core.mixin.access.LevelResourceAccessor;
import com.blackgear.platform.core.network.listener.ServerListenerEvents;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    public static final Logger LOGGER = LogManager.getLogger();
    static final LevelResource SERVERCONFIG = LevelResourceAccessor.createLevelResource("serverconfig");
    public static final ResourceLocation CONFIG_SYNC = new ResourceLocation(Platform.MOD_ID, "config_sync");

    private static Path getServerConfigPath(MinecraftServer server) {
        Path config = server.getWorldPath(SERVERCONFIG);
        getOrCreateDirectory(config, "serverconfig");
        return config;
    }

    public static void bootstrap() {
        ServerLifecycleEvents.STARTING.register(server -> ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server)));
        ServerLifecycleEvents.STOPPING.register(server -> ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server)));

        ServerListenerEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            if (server.isSingleplayerOwner(player.getGameProfile())) return;

            ConfigTracker.INSTANCE.configSets().get(ModConfig.Type.SERVER).forEach(config -> {
                try {
                    String name = config.getFileName();
                    byte[] data = Files.readAllBytes(config.getFullPath());

                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeUtf(name);
                    buf.writeByteArray(data);
                    sender.sendPacket(CONFIG_SYNC, buf);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
        });
    }

    private static void getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath.getParent())) {
            getOrCreateDirectory(dirPath.getParent(), "parent of " + dirLabel);
        }
        
        if (!Files.isDirectory(dirPath)) {
            LOGGER.debug("Making {} directory : {}", dirLabel, dirPath);
            
            try {
                Files.createDirectory(dirPath);
            } catch (IOException exception) {
                if (exception instanceof FileAlreadyExistsException) {
                    LOGGER.fatal("Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    LOGGER.fatal("Problem with creating {} directory (Permissions?)", dirLabel, exception);
                }
                throw new RuntimeException("Problem creating directory", exception);
            }
            
            LOGGER.debug("Created {} directory : {}", dirLabel, dirPath);
        } else {
            LOGGER.debug("Found existing {} directory : {}", dirLabel, dirPath);
        }
    }
}