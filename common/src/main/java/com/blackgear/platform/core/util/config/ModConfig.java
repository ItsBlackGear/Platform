package com.blackgear.platform.core.util.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.nio.file.Path;
import java.util.Locale;

public class ModConfig {
    private final Type type;
    private final IConfigSpec<?> spec;
    private final String fileName;
    private final String modId;
    private final ConfigFileTypeHandler configHandler = ConfigFileTypeHandler.TOML;
    private CommentedConfig configData;
    
    public ModConfig(final Type type, final IConfigSpec<?> spec, final String modId, final String fileName) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        this.modId = modId;
        ConfigTracker.INSTANCE.trackConfig(this);
    }

    public Type getType() {
        return type;
    }
    
    public String getFileName() {
        return fileName;
    }

    public ConfigFileTypeHandler getHandler() {
        return this.configHandler;
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
        if (this.configData instanceof FileConfig config) config.save();
    }
    
    public Path getFullPath() {
        return this.configData instanceof FileConfig config ? config.getNioPath() : null;
    }

    public enum Type {
        /**
         * Common mod config for configuration that needs to be loaded on both environments.
         * <p>Loaded on both servers and clients.
         * <p>Stored in the global config directory.
         * <p>Not synced.
         * <p>Suffix is "-common".
         */
        COMMON,
        /**
         * Client config is for configuration affecting the ONLY client state such as graphical options.
         * <p>Only loaded on the client side.
         * <p>Stored in the global config directory.
         * <p>Not synced.
         * <p>Suffix is "-client".
         */
        CLIENT,
        /**
         * Server type config is configuration that is associated with a server instance.
         * <p>Only loaded during server startup.
         * <p>Stored in a server/save specific "serverconfig" directory.
         * <p>Synced to clients during connection.
         * <p>Suffix is "-server".
         */
        SERVER;
        
        public String extension() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}