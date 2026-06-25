package com.blackgear.platform.core.mixin.forge.access;

import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;

@org.spongepowered.asm.mixin.Mixin(net.minecraftforge.fml.ModContainer.class)
public interface ModContainerAccessor {
    @Accessor
    EnumMap<ModConfig.Type, ModConfig> getConfigs();
}
