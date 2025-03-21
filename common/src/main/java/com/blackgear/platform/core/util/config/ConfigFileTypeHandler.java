package com.blackgear.platform.core.util.config;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ConfigEvents;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static com.blackgear.platform.core.util.config.ConfigTracker.CONFIG;

public class ConfigFileTypeHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    static ConfigFileTypeHandler TOML = new ConfigFileTypeHandler();
    private static final Path defaultConfigPath = Environment.getGameDir().resolve("defaultconfigs");
    
    public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
        return (c) -> {
            final Path configPath = configBasePath.resolve(c.getFileName());
            final CommentedFileConfig configData = CommentedFileConfig.builder(configPath, TomlFormat.instance()).sync().
                preserveInsertionOrder().
                autosave().
                onFileNotFound((newfile, configFormat) -> setupConfigFile(c, newfile, configFormat)).
                writingMode(WritingMode.REPLACE).
                build();
            LOGGER.debug(CONFIG,
                "Built TOML config for {}",
                configPath.toString()
            );
            
            try {
                configData.load();
            } catch (ParsingException ex) {
                throw new ConfigLoadingException(c, ex);
            }
            
            LOGGER.debug(CONFIG,
                "Loaded TOML config file {}",
                configPath.toString()
            );
            
            try {
                FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(c, configData, Thread.currentThread().getContextClassLoader()));
                LOGGER.debug(CONFIG,
                    "Watching TOML config file {} for changes",
                    configPath.toString()
                );
            } catch (IOException exception) {
                throw new RuntimeException("Couldn't watch config file", exception);
            }
            
            return configData;
        };
    }
    
    public void unload(Path configBasePath, ModConfig config) {
        Path configPath = configBasePath.resolve(config.getFileName());
        try {
            FileWatcher.defaultInstance().removeWatch(configBasePath.resolve(config.getFileName()));
        } catch (RuntimeException exception) {
            LOGGER.error(
                "Failed to remove config {} from tracker!",
                configPath.toString(),
                exception
            );
        }
    }
    
    private boolean setupConfigFile(final ModConfig modConfig, final Path file, final ConfigFormat<?> conf) throws IOException {
        Files.createDirectories(file.getParent());
        Path path = defaultConfigPath.resolve(modConfig.getFileName());
        if (Files.exists(path)) {
            LOGGER.info(CONFIG,
                "Loading default config file from path {}",
                path
            );
            
            Files.copy(path, file);
        } else {
            Files.createFile(file);
            conf.initEmptyFile(file);
        }
        
        return true;
    }
    
    public static void backUpConfig(final CommentedFileConfig commentedFileConfig) {
        backUpConfig(commentedFileConfig, 5);
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
            LOGGER.warn(CONFIG,
                "Failed to back up config file {}",
                commentedFileConfig.getNioPath(),
                exception
            );
        }
    }
    
    
    private static class ConfigWatcher implements Runnable {
        private final ModConfig modConfig;
        private final CommentedFileConfig commentedFileConfig;
        private final ClassLoader realClassLoader;
        
        ConfigWatcher(final ModConfig modConfig, final CommentedFileConfig commentedFileConfig, final ClassLoader classLoader) {
            this.modConfig = modConfig;
            this.commentedFileConfig = commentedFileConfig;
            this.realClassLoader = classLoader;
        }
        
        @Override
        public void run() {
            // Force the regular classloader onto the special thread
            Thread.currentThread().setContextClassLoader(this.realClassLoader);
            if (!this.modConfig.getSpec().isCorrecting()) {
                try {
                    this.commentedFileConfig.load();
                    if (!this.modConfig.getSpec().isCorrect(this.commentedFileConfig)) {
                        LOGGER.warn(CONFIG,
                            "Configuration file {} is not correct. Correcting",
                            this.commentedFileConfig.getFile().getAbsolutePath()
                        );
                        
                        ConfigFileTypeHandler.backUpConfig(this.commentedFileConfig);
                        this.modConfig.getSpec().correct(this.commentedFileConfig);
                        this.commentedFileConfig.save();
                    }
                } catch (ParsingException ex) {
                    throw new ConfigLoadingException(modConfig, ex);
                }
                
                LOGGER.debug(CONFIG,
                    "Config file {} changed, sending notifies",
                    this.modConfig.getFileName()
                );
                
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
}