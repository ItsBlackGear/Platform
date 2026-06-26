package com.blackgear.platform.core.util.config;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.mixin.access.LevelResourceAccessor;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.google.common.collect.ImmutableMap;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.FilenameUtils;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigLoader {
    static final LevelResource SERVERCONFIG = LevelResourceAccessor.createLevelResource("serverconfig");
    private static final String DEFAULTCONFIGS = "defaultconfigs";
    public static final Map<String, Map<String, Object>> DEFAULT_CONFIG_VALUES = new ConcurrentHashMap<>();

    private static void getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath.getParent())) {
            getOrCreateDirectory(dirPath.getParent(), "parent of " + dirLabel);
        }
        if (!Files.isDirectory(dirPath)) {
            Platform.LOGGER.debug("Making {} directory : {}", dirLabel, dirPath);
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                if (e instanceof FileAlreadyExistsException) {
                    Platform.LOGGER.error("Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    Platform.LOGGER.error("Problem with creating {} directory (Permissions?)", dirLabel, e);
                }
                throw new RuntimeException("Problem creating directory", e);
            }
            Platform.LOGGER.debug("Created {} directory : {}", dirLabel, dirPath);
        } else {
            Platform.LOGGER.debug("Found existing {} directory : {}", dirLabel, dirPath);
        }
    }

    public static Path getServerConfigDirectory(MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVERCONFIG);
        getOrCreateDirectory(serverConfig, "server config directory");
        return serverConfig;
    }

    public static Path getDefaultConfigsDirectory() {
        Path defaultConfigs = Environment.getGameDir().resolve(DEFAULTCONFIGS);
        getOrCreateDirectory(defaultConfigs, "default configs directory");
        return defaultConfigs;
    }

    public static void tryLoadConfigFile(FileConfig config) {
        try {
            config.load();
        } catch (ParsingException e) {
            try {
                backUpConfig(config.getNioPath(), 5);
                Files.delete(config.getNioPath());
                config.load();
                Platform.LOGGER.warn("Configuration file {} could not be parsed. Correcting", config.getNioPath());
                return;
            } catch (Throwable t) {
                e.addSuppressed(t);
            }
            throw e;
        }
    }
    public static void tryRegisterDefaultConfig(ModConfig modConfig) {
        String fileName = modConfig.getFileName();
        Path path = getDefaultConfigsDirectory().resolve(fileName);
        if (Files.exists(path)) {
            try (CommentedFileConfig config = CommentedFileConfig.of(path)) {
                config.load();
                // just get the values map, no need to hold on to the resource itself
                Map<String, Object> values = config.valueMap();
                if (values != null && !values.isEmpty()) {
                    DEFAULT_CONFIG_VALUES.put(fileName.intern(), ImmutableMap.copyOf(values));
                }
                Platform.LOGGER.debug("Loaded default config values for future corrections from file at path {}", path);
            } catch (Exception ignored) {

            }
        }
    }

    public static void backUpConfig(final Path commentedFileConfig, final int maxBackups) {
        if (!Files.exists(commentedFileConfig)) return;
        Path bakFileLocation = commentedFileConfig.getParent();
        String bakFileName = FilenameUtils.removeExtension(commentedFileConfig.getFileName().toString());
        String bakFileExtension = FilenameUtils.getExtension(commentedFileConfig.getFileName().toString()) + ".bak";
        Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
        try {
            for (int i = maxBackups; i > 0; i--) {
                Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
                if (Files.exists(oldBak)) {
                    if (i >= maxBackups) Files.delete(oldBak);
                    else
                        Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
                }
            }
            Files.copy(commentedFileConfig, bakFile);
        } catch (IOException exception) {
            Platform.LOGGER.warn("Failed to back up config file {}", commentedFileConfig, exception);
        }
    }
    
    @ExpectPlatform
    public static void bootstrap() { /* NO-OP */ }
}