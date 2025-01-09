package com.blackgear.platform.common.worldgen.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Represents a biome and its corresponding weight.
 */
public final class WeightedBiomeEntry {
    private final ResourceKey<Biome> biome;
    private final double weight;
    private final double upperWeightBound;
    
    /**
     * @param biome the biome
     * @param weight how often a biome will be chosen
     * @param upperWeightBound the upper weight bound within the context of the other entries, used for the binary search
     */
    WeightedBiomeEntry(final ResourceKey<Biome> biome, final double weight, final double upperWeightBound) {
        this.biome = biome;
        this.weight = weight;
        this.upperWeightBound = upperWeightBound;
    }
    
    ResourceKey<Biome> getBiome() {
        return this.biome;
    }
    
    double getWeight() {
        return this.weight;
    }
    
    /**
     * @return the upper weight boundary for the search
     */
    double getUpperWeightBound() {
        return this.upperWeightBound;
    }
}