package com.blackgear.platform.common.worldgen.biome;

import com.blackgear.platform.common.worldgen.parameters.Temperature;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.context.Context;

import java.util.*;

/**
 * Deals with picking variants for you.
 */
public final class VariantTransformer {
    private final SubTransformer defaultTransformer = new SubTransformer();
    private final Map<Temperature, SubTransformer> transformers = new HashMap<>();
    
    /**
     * @param variant the variant that the replaced biome is replaced with
     * @param chance the chance of replacement of the biome into the variant
     * @param climates the climates that the variant can replace the base biome in, empty/null indicates all climates
     */
    void addBiome(ResourceKey<Biome> variant, double chance, Temperature[] climates) {
        if (climates == null || climates.length == 0) {
            defaultTransformer.addBiome(variant, chance);
            climates = Temperature.values();
        }
        
        for (Temperature climate : climates) {
            transformers.computeIfAbsent(climate, c -> new SubTransformer()).addBiome(variant, chance);
        }
    }
    
    /**
     * Transforms a biome into a variant randomly depending on its chance.
     *
     * @param replaced biome to transform
     * @param random the {@link Context} from the layer
     * @return the transformed biome
     */
    ResourceKey<Biome> transformBiome(ResourceKey<Biome> replaced, Context random, Temperature climate) {
        if (climate == null) {
            return defaultTransformer.transformBiome(replaced, random);
        }

        SubTransformer transformer = transformers.get(climate);

        if (transformer != null) {
            return transformer.transformBiome(replaced, random);
        } else {
            return replaced;
        }
    }
    
    static final class SubTransformer {
        private final List<BiomeVariant> variants = new ArrayList<>();
        
        /**
         * @param variant the variant that the replaced biome is replaced with
         * @param chance the chance of replacement of the biome into the variant
         */
        private void addBiome(ResourceKey<Biome> variant, double chance) {
            variants.add(new BiomeVariant(variant, chance));
        }
        
        /**
         * Transforms a biome into a variant randomly depending on its chance.
         *
         * @param replaced biome to transform
         * @param random the {@link Context} from the layer
         * @return the transformed biome
         */
        private ResourceKey<Biome> transformBiome(ResourceKey<Biome> replaced, Context random) {
            for (BiomeVariant variant : variants) {
                if (random.nextRandom(Integer.MAX_VALUE) < variant.getChance() * Integer.MAX_VALUE) {
                    return variant.getVariant();
                }
            }
            
            return replaced;
        }
    }
}