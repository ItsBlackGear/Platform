package com.blackgear.platform.core.util.config.neoforge;

import com.blackgear.platform.core.neoforge.EnvironmentImpl;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public class ModConfigImpl implements com.blackgear.platform.core.util.config.ModConfig {
    private final ModConfig config;

    public ModConfigImpl(ModConfig config) {
        this.config = config;
    }

    @Override
    public Type getType() {
        return EnvironmentImpl.ForgeConfigHandler.convert(this.config.getType());
    }

    @Override
    public String getFileName() {
        return this.config.getFileName();
    }

    @Override
    public UnmodifiableConfig getSpec() {
        return ((ModConfigSpec) this.config.getSpec()).getValues();
    }

    @Override
    public String getModId() {
        return this.config.getModId();
    }

    @Override
    public CommentedConfig getConfigData() {
        IConfigSpec.ILoadedConfig config = this.config.getLoadedConfig();
        return config != null ? config.config() : null;
    }

    @Override
    public void save() {
        IConfigSpec.ILoadedConfig config = this.config.getLoadedConfig();
        if (config != null) config.save();
    }

    @Override
    public Path getFullPath() {
        return this.config.getFullPath();
    }
}