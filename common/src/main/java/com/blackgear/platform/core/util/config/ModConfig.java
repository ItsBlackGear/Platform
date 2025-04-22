package com.blackgear.platform.core.util.config;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ConfigEvents;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Function;

public class ModConfig {
    private final Type type;
    private final IConfigSpec<?> spec;
    private final String fileName;
    private final String modId;
    private CommentedConfig configData;
    
    public ModConfig(final Type type, final IConfigSpec<?> spec, final String modId, final String fileName) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        this.modId = modId;
        ConfigTracker.INSTANCE.trackConfig(this);
    }
    
    public ModConfig(final Type type, final IConfigSpec<?> spec, final String modId) {
        this(type, spec, modId, defaultConfigName(type, modId));
    }
    
    private static String defaultConfigName(Type type, String modId) {
        return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
    }
    
    public Type getType() {
        return type;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void unload(Path configBasePath) {
        Path configPath = configBasePath.resolve(getFileName());
        try {
            FileWatcher.defaultInstance().removeWatch(configBasePath.resolve(getFileName()));
        } catch (RuntimeException exception) {
            ConfigTracker.LOGGER.error("Failed to remove config {} from tracker!", configPath, exception);
        }
    }

    private boolean setupConfigFile(final ModConfig modConfig, final Path file, final ConfigFormat<?> format) throws IOException {
        Files.createDirectories(file.getParent());
        Path path = Environment.getConfigDir().resolve(modConfig.getFileName());
        if (Files.exists(path)) {
            ConfigTracker.LOGGER.info(ConfigTracker.CONFIG, "Loading default config file from path {}", path);
            Files.copy(path, file);
        } else {
            Files.createFile(file);
            format.initEmptyFile(file);
        }

        return true;
    }

    public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
        return config -> {
            final Path configPath = configBasePath.resolve(config.getFileName());
            final CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync()
                .preserveInsertionOrder()
                .autosave()
                .onFileNotFound((file, format) -> setupConfigFile(config, file, format))
                .writingMode(WritingMode.REPLACE)
                .build();
            ConfigTracker.LOGGER.debug(ConfigTracker.CONFIG, "Built TOML config for {}", configPath.toString());
            try {
                configData.load();
            } catch (ParsingException exception) {
                throw new ConfigLoadingException(config, exception);
            }
            ConfigTracker.LOGGER.debug(ConfigTracker.CONFIG, "Loaded TOML config file {}", configPath.toString());
            try {
                FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(config, configData, Thread.currentThread().getContextClassLoader()));
                ConfigTracker.LOGGER.debug(ConfigTracker.CONFIG, "Watching TOML config file {} for changes", configPath.toString());
            } catch (IOException exception) {
                throw new RuntimeException("Couldn't watch config file", exception);
            }

            return configData;
        };
    }
    
    @SuppressWarnings("unchecked")
    public <T extends IConfigSpec<T>> IConfigSpec<T> getSpec() {
        return (IConfigSpec<T>) spec;
    }
    
    public String getModId() {
        return this.modId;
    }
    
    public CommentedConfig getConfigData() {
        return this.configData;
    }
    
    void setConfigData(final CommentedConfig configData) {
        this.configData = configData;
        this.spec.setConfig(this.configData);
    }
    
    public void save() {
        ((CommentedFileConfig) this.configData).save();
    }
    
    public Path getFullPath() {
        return ((CommentedFileConfig) this.configData).getNioPath();
    }
    
    public void acceptSyncedConfig(byte[] bytes) {
        this.setConfigData(TomlFormat.instance().createParser().parse(new ByteArrayInputStream(bytes)));
        ConfigEvents.RELOADING.invoker().onModConfig(this);
    }

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig, final int maxBackups) {
        Path bakFileLocation = commentedFileConfig.getNioPath().getParent();
        String bakFileName = FilenameUtils.removeExtension(commentedFileConfig.getFile().getName());
        String bakFileExtension = FilenameUtils.getExtension(commentedFileConfig.getFile().getName()) + ".bak";
        Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
        try {
            for (int i = maxBackups; i > 0; i--) {
                Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
                if (Files.exists(oldBak)) {
                    if (i >= maxBackups) {
                        Files.delete(oldBak);
                    } else {
                        Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
                    }
                }
            }
            Files.copy(commentedFileConfig.getNioPath(), bakFile);
        } catch (IOException exception) {
            ConfigTracker.LOGGER.warn(ConfigTracker.CONFIG, "Failed to back up config file {}", commentedFileConfig);
        }
    }

    private record ConfigWatcher(
        ModConfig modConfig,
        CommentedFileConfig commentedFileConfig,
        ClassLoader realClassLoader
    ) implements Runnable {
        @Override
        public void run() {
            Thread.currentThread().setContextClassLoader(this.realClassLoader);
            if (!this.modConfig.getSpec().isCorrecting()) {
                try {
                    this.commentedFileConfig.load();
                    if (!this.modConfig.getSpec().isCorrect(this.commentedFileConfig)) {
                        ConfigTracker.LOGGER.warn(ConfigTracker.CONFIG, "Configuration file {} is not correct. Correcting", this.commentedFileConfig.getFile().getAbsolutePath());
                        backUpConfig(this.commentedFileConfig, 5);
                        this.modConfig.getSpec().correct(this.commentedFileConfig);
                        this.commentedFileConfig.save();
                    }
                } catch (ParsingException exception) {
                    throw new ConfigLoadingException(this.modConfig, exception);
                }
                ConfigTracker.LOGGER.debug(ConfigTracker.CONFIG, "Config file {} changed, sending notifies", this.modConfig.getFileName());
                this.modConfig.getSpec().afterReload();
                ConfigEvents.RELOADING.invoker().onModConfig(this.modConfig);
            }
        }
    }

    private static class ConfigLoadingException extends RuntimeException {
        public ConfigLoadingException(ModConfig config, Exception cause) {
            super("Failed loading config file " + config.getFileName() + " of type " + config.getType() + " for modid " + config.getModId(), cause);
        }
    }

    public enum Type {
        /**
         * Common mod config for configuration that needs to be loaded on both environments.
         * Loaded on both servers and clients.
         * Stored in the global config directory.
         * Not synced.
         * Suffix is "-common" by default.
         */
        COMMON,
        /**
         * Client config is for configuration affecting the ONLY client state such as graphical options.
         * Only loaded on the client side.
         * Stored in the global config directory.
         * Not synced.
         * Suffix is "-client" by default.
         */
        CLIENT,
        /**
         * Server type config is configuration that is associated with a server instance.
         * Only loaded during server startup.
         * Stored in a server/save specific "serverconfig" directory.
         * Synced to clients during connection.
         * Suffix is "-server" by default.
         */
        SERVER;
        
        public String extension() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}