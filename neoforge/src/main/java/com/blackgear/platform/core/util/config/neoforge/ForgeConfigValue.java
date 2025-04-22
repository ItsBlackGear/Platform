package com.blackgear.platform.core.util.config.neoforge;

import com.blackgear.platform.core.util.config.ConfigBuilder;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ForgeConfigValue<T> implements ConfigBuilder.ConfigValue<T> {
    private final ModConfigSpec.ConfigValue<T> value;
    
    public ForgeConfigValue(ModConfigSpec.ConfigValue<T> value) {
        this.value = value;
    }
    
    @Override
    public List<String> getPath() {
        return this.value.getPath();
    }
    
    @Override
    public T get() {
        return this.value.get();
    }
    
    @Override
    public ConfigBuilder next() {
        return new ForgeConfigBuilder(this.value.next());
    }
    
    @Override
    public void save() {
        this.value.save();
    }
    
    @Override
    public void set(T value) {
        this.value.set(value);
    }
    
    @Override
    public void clearCache() {
        this.value.clearCache();
    }
}