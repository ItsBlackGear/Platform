package com.blackgear.platform.core.util.config;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ServerLifecycleEvents;
import com.blackgear.platform.core.mixin.access.LevelResourceAccessor;
import com.blackgear.platform.core.util.network.PacketByteBufs;
import com.blackgear.platform.core.util.network.client.ClientLoginNetworking;
import com.blackgear.platform.core.util.network.server.ServerLoginConnectionEvents;
import com.blackgear.platform.core.util.network.server.ServerLoginNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.blackgear.platform.core.util.config.ConfigTracker.CORE;

public class ConfigLoader {
    static final Marker NETWORK = MarkerManager.getMarker("NETWORK");
    static final Marker HANDSHAKE = MarkerManager.getMarker("HANDSHAKE").setParents(NETWORK);
    static final Logger LOGGER = LogManager.getLogger();
    static final LevelResource SERVERCONFIG = LevelResourceAccessor.createLevelResource("serverconfig");
    
    static final ResourceLocation SYNC_CONFIGS_CHANNEL = new ResourceLocation(Platform.MOD_ID, "sync_configs");
    static final ResourceLocation MODDED_CONNECTION_CHANNEL = new ResourceLocation(Platform.MOD_ID, "modded_connection");
    
    static final ConfigLoader INSTANCE = new ConfigLoader(ConfigTracker.INSTANCE);
    
    private static boolean isVanillaConnection = true;
    private final ConfigTracker tracker;
    
    private ConfigLoader(ConfigTracker tracker) {
        this.tracker = tracker;
    }
    
    public static void bootstrap() {
        INSTANCE.setupClient();
        INSTANCE.setupServer();
        INSTANCE.loadDefaultConfigPath();
        INSTANCE.loadServerConfig();
    }
    
    private void setupClient() {
        if (Environment.isClientSide()) {
            ClientLoginNetworking.registerGlobalReceiver(SYNC_CONFIGS_CHANNEL, (client, listener, buffer, exporter) -> {
                String fileName = INSTANCE.receiveSyncedConfig(buffer);
                LOGGER.debug(HANDSHAKE, "Received config sync for {} from server", fileName);
                FriendlyByteBuf response = PacketByteBufs.create();
                response.writeUtf(fileName);
                LOGGER.debug(HANDSHAKE, "Sent config sync for {} to server", fileName);
                
                return CompletableFuture.completedFuture(response);
            });
            
            ClientLoginNetworking.registerGlobalReceiver(MODDED_CONNECTION_CHANNEL, (client, listener, buffer, exporter) -> {
                LOGGER.debug(HANDSHAKE, "Received modded connection marker from server");
                setModdedConnection();
                
                return CompletableFuture.completedFuture(PacketByteBufs.create());
            });
        }
    }
    
    private void setupServer() {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            List<Pair<String, FriendlyByteBuf>> pairs = this.syncConfigs();
            
            pairs.forEach(pair -> synchronizer.waitFor(server.submit(() -> sender.sendPacket(SYNC_CONFIGS_CHANNEL, pair.getValue()))));
            
            synchronizer.waitFor(server.submit(() -> sender.sendPacket(MODDED_CONNECTION_CHANNEL, PacketByteBufs.create())));
        });
        
        ServerLoginNetworking.registerGlobalReceiver(SYNC_CONFIGS_CHANNEL, (server, handler, understood, buffer, synchronizer, sender) -> {
            if (understood) {
                String fileName = buffer.readUtf(32767);
                LOGGER.debug(HANDSHAKE, "Received acknowledgement for config sync for {} from client", fileName);
            }
        });
        
        ServerLoginNetworking.registerGlobalReceiver(MODDED_CONNECTION_CHANNEL, (server, handler, understood, buffer, synchronizer, sender) -> {
            LOGGER.debug(HANDSHAKE, "Received acknowledgement for modded connection marker from client");
        });
    }
    
    private void loadServerConfig() {
        ServerLifecycleEvents.STARTING.register(server -> {
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
        });
        
        ServerLifecycleEvents.STOPPED.register(server -> {
            ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
        });
    }
    
    private void loadDefaultConfigPath() {
        LOGGER.trace("Default config paths at {}", "defaultconfigs");
        this.getOrCreateDirectory(Environment.getGameDir().resolve("defaultconfigs"), "default config directory");
    }
    
    public String receiveSyncedConfig(FriendlyByteBuf buffer) {
        String fileName = buffer.readUtf(32767);
        byte[] fileData = buffer.readByteArray();
        
        if (!Minecraft.getInstance().isLocalServer()) {
            Optional.ofNullable(this.tracker.fileMap().get(fileName)).ifPresent(config -> config.acceptSyncedConfig(fileData));
        }
        
        return fileName;
    }
    
    private List<Pair<String, FriendlyByteBuf>> syncConfigs() {
        Map<String, byte[]> data = this.tracker.configSets()
            .get(ModConfig.Type.SERVER)
            .stream()
            .collect(Collectors.toMap(ModConfig::getFileName, config -> {
                try {
                    return Files.readAllBytes(config.getFullPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
        );
        
        return data.entrySet().stream().map(e -> {
            FriendlyByteBuf buffer = PacketByteBufs.create();
            buffer.writeUtf(e.getKey());
            buffer.writeByteArray(e.getValue());
            return Pair.of("Config " + e.getKey(), buffer);
        }).collect(Collectors.toList());
    }
    
    private void getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath.getParent())) {
            this.getOrCreateDirectory(dirPath.getParent(), "parent of " + dirLabel);
        }
        
        if (!Files.isDirectory(dirPath)) {
            LOGGER.debug(CORE, "Making {} directory : {}", dirLabel, dirPath);
            
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                if (e instanceof FileAlreadyExistsException) {
                    LOGGER.fatal(CORE, "Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    LOGGER.fatal(CORE, "Problem with creating {} directory (Permissions?)", dirLabel, e);
                }
                throw new RuntimeException("Problem creating directory", e);
            }
            
            LOGGER.debug(CORE, "Created {} directory : {}", dirLabel, dirPath);
        } else {
            LOGGER.debug(CORE, "Found existing {} directory : {}", dirLabel, dirPath);
        }
    }
    
    public static void setModdedConnection() {
        isVanillaConnection = false;
    }
    
    private static void setVanillaConnection() {
        isVanillaConnection = true;
    }
    
    public static boolean isVanillaConnection(Connection manager) {
        return isVanillaConnection;
    }
    
    public static void handleClientLoginSuccess(Connection manager) {
        if (isVanillaConnection(manager)) {
            LOGGER.info("Connected to a vanilla server. Catching up missing behaviour.");
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        } else {
            // reset for next server
            setVanillaConnection();
            LOGGER.info("Connected to a modded server.");
        }
    }
    
    private static Path getServerConfigPath(final MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVERCONFIG);
        INSTANCE.getOrCreateDirectory(serverConfig, "server config directory");
        return serverConfig;
    }
}