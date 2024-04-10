package com.blackgear.platform.common.worldgen.modifier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Utility class designed to identify the biome or biomes for implementing biome-specific features.
 **/
public interface BiomeContext {
    boolean is(TagKey<Biome> tag);

    boolean is(ResourceKey<Biome> biome);
}