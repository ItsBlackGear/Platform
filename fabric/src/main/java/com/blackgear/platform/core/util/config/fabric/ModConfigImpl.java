package com.blackgear.platform.core.util.config.fabric;

import com.blackgear.platform.core.util.config.ModConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;

public class ModConfigImpl implements ModConfig {
    private final Type type;
    private final FabricConfigSpec spec;
    private final String fileName;
    private final ModContainer container;
    private final ConfigFileTypeHandler configHandler;
    private CommentedConfig configData;

    public ModConfigImpl(Type type, FabricConfigSpec spec, ModContainer container, String fileName) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        this.container = container;
        this.configHandler = ConfigFileTypeHandler.TOML;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public ConfigFileTypeHandler getHandler() {
        return configHandler;
    }

    @Override
    public FabricConfigSpec getSpec() {
        return spec;
    }

    @Override
    public String getModId() {
        return this.container.getMetadata().getId();
    }

    @Override
    public CommentedConfig getConfigData() {
        return this.configData;
    }

    void setConfigData(final CommentedConfig configData) {
        this.configData = configData;
        this.spec.setConfig(this.configData);
    }

    @Override
    public void save() {
        if (this.configData instanceof FileConfig)
            ((FileConfig) this.configData).save();
    }

    @Override
    public Path getFullPath() {
        return this.configData instanceof FileConfig ? ((FileConfig) this.configData).getNioPath() : null;
    }
}