package com.blackgear.platform.core.util.config;

import com.blackgear.platform.core.events.ConfigEvent;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.Callable;

public class ModConfig {
    private final Type type;
    private final IConfigSpec<?> spec;
    private final String fileName;
    private final String modId;
    private final ConfigFileTypeHandler configHandler;
    private CommentedConfig configData;
    private Callable<Void> saveHandler;
    
    public ModConfig(final Type type, final IConfigSpec<?> spec, final String modId, final String fileName) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        this.modId = modId;
        this.configHandler = ConfigFileTypeHandler.TOML;
        ConfigTracker.INSTANCE.trackConfig(this);
    }
    
    public ModConfig(final Type type, final IConfigSpec<?> spec, final String modId) {
        this(type, spec, modId, defaultConfigName(type, modId));
    }
    
    private static String defaultConfigName(Type type, String modId) {
        return String.format("%s-%s.toml", modId, type.extension());
    }
    
    public Type getType() {
        return type;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public ConfigFileTypeHandler getHandler() {
        return configHandler;
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
        this.spec.acceptConfig(this.configData);
    }
    
    public void save() {
        ((CommentedFileConfig) this.configData).save();
    }
    
    public Path getFullPath() {
        return ((CommentedFileConfig) this.configData).getNioPath();
    }
    
    public void acceptSyncedConfig(byte[] bytes) {
        this.setConfigData(TomlFormat.instance().createParser().parse(new ByteArrayInputStream(bytes)));
        ConfigEvent.RELOADING.invoker().onModConfigReloading(this);
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