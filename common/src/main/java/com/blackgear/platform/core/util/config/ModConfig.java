package com.blackgear.platform.core.util.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Locale;

public interface ModConfig {
    Type getType();
    
    String getFileName();
    
    UnmodifiableConfig getSpec();
    
    String getModId();
    
    CommentedConfig getConfigData();
    
    void save();
    
    @Nullable Path getFullPath();
    
    enum Type {
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