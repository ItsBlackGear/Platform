package com.blackgear.platform.core.mixin.access;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(OverworldBiomeSource.class)
public interface OverworldBiomeSourceAccessor {
    @Accessor("POSSIBLE_BIOMES")
    static List<ResourceKey<Biome>> getPossibleBiomes() {
        throw new UnsupportedOperationException();
    }
    
    @Mutable @Accessor("POSSIBLE_BIOMES")
    static void setPossibleBiomes(List<ResourceKey<Biome>> biomes) {
        throw new UnsupportedOperationException();
    }
}
