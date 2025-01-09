package com.blackgear.platform.common.worldgen.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Represents a biome variant and its corresponding chance.
 */
final class BiomeVariant {
    private final ResourceKey<Biome> variant;
    private final double chance;
    
    /**
     * @param variant the variant biome
     * @param chance the chance of replacement of the biome into the variant
     */
    BiomeVariant(final ResourceKey<Biome> variant, final double chance) {
        this.variant = variant;
        this.chance = chance;
    }
    
    /**
     * @return the variant biome
     */
    ResourceKey<Biome> getVariant() {
        return variant;
    }
    
    /**
     * @return the chance of replacement of the biome into the variant
     */
    double getChance() {
        return chance;
    }
}