package com.blackgear.platform.common.worldgen.biome;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.worldgen.parameters.Temperature;
import com.blackgear.platform.core.mixin.access.BiomesAccessor;
import com.blackgear.platform.core.mixin.forge.core.access.RegionHillsLayerAccessor;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.context.Context;

import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class InternalBiomeUtils {
    /**
     * @param north  raw id of the biome to the north
     * @param east   raw id of the biome to the east
     * @param south  raw id of the biome to the south
     * @param west   raw id of the biome to the west
     * @param center central biome that comparisons are relative to
     * @return whether the central biome is an edge of a biome
     */
    public static boolean isEdge(int north, int east, int south, int west, int center) {
        return areUnsimilar(center, north) || areUnsimilar(center, east) || areUnsimilar(center, south) || areUnsimilar(center, west);
    }
    
    /**
     * @param mainBiomeId      the main raw biome id in comparison
     * @param secondaryBiomeId the secondary raw biome id in comparison
     * @return whether the two biomes are unsimilar
     */
    private static boolean areUnsimilar(int mainBiomeId, int secondaryBiomeId) {
        if (mainBiomeId == secondaryBiomeId) { // for efficiency, determine if the ids are equal first
            return false;
        } else {
            // Regard a biome as "similar" to it's derived biome, i.e.
            // No edge between plains and sunflower plains
            
            // The parent-child relationship previously modeled in Biome itself is gone,
            // and has been - for the time being - replaced by a hardcoded raw-id map
            // in AddHillsLayer.
            Int2IntMap parentChildMap = RegionHillsLayerAccessor.getMUTATIONS();
            return parentChildMap.get(mainBiomeId) != secondaryBiomeId
                && parentChildMap.get(secondaryBiomeId) != mainBiomeId;
        }
    }
    
    /**
     * @param north raw id of the biome to the north
     * @param east  raw id of the biome to the east
     * @param south raw id of the biome to the south
     * @param west  raw id of the biome to the west
     * @return whether a biome in any direction is an ocean around the central biome
     */
    public static boolean neighborsOcean(int north, int east, int south, int west) {
        return isOceanBiome(north) || isOceanBiome(east) || isOceanBiome(south) || isOceanBiome(west);
    }
    
    private static boolean isOceanBiome(int id) {
        Biome biome = BuiltinRegistries.BIOME.byId(id);
        return biome != null && biome.getBiomeCategory() == Biome.BiomeCategory.OCEAN;
    }
    
    public static int searchForBiome(double reqWeightSum, int vanillaArrayWeight, List<WeightedBiomeEntry> moddedBiomes) {
        reqWeightSum -= vanillaArrayWeight;
        int low = 0;
        int high = moddedBiomes.size() - 1;
        
        while (low < high) {
            int mid = (high + low) >>> 1;
            
            if (reqWeightSum < moddedBiomes.get(mid).getUpperWeightBound()) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        
        return low;
    }
    
    /**
     * Potentially transforms a biome into its variants based on the provided randomness source.
     *
     * @param random   The randomness source
     * @param existing The base biome
     * @param climate  The climate in which the biome resides, or null to indicate an unknown climate
     * @return The potentially transformed biome
     */
    public static int transformBiome(Context random, ResourceKey<Biome> existing, Temperature climate) {
        Map<ResourceKey<Biome>, VariantTransformer> overworldVariantTransformers = InternalBiomeData.getOverworldVariantTransformers();
        VariantTransformer transformer = overworldVariantTransformers.get(existing);
        
        if (transformer != null) {
            ResourceKey<Biome> key = transformer.transformBiome(existing, random, climate);
            return getRawId(key);
        }
        
        return getRawId(existing);
    }
    
    public static void injectBiomesIntoClimate(Context random, int[] vanillaArray, Temperature climate, IntConsumer result) {
        WeightedBiomePicker picker = InternalBiomeData.getOverworldModdedContinentalBiomePickers().get(climate);
        
        if (picker == null || picker.getCurrentWeightTotal() <= 0.0) {
            // Return early, there are no modded biomes.
            // Since we don't pass any values to the IntConsumer, this falls through to vanilla logic.
            // Thus, this prevents Fabric from changing vanilla biome selection behavior without biome mods in this case.
            
            return;
        }
        
        int vanillaArrayWeight = vanillaArray.length;
        double reqWeightSum = random.nextRandom(Integer.MAX_VALUE) * (vanillaArray.length + picker.getCurrentWeightTotal()) / Integer.MAX_VALUE;
        
        if (reqWeightSum < vanillaArray.length) {
            // Vanilla biome; look it up from the vanilla array and transform accordingly.
            
            result.accept(transformBiome(random, Biomes.byId(vanillaArray[(int) reqWeightSum]), climate));
        } else {
            // Modded biome; use a binary search, and then transform accordingly.
            
            WeightedBiomeEntry found = picker.search(reqWeightSum - vanillaArrayWeight);
            
            result.accept(transformBiome(random, found.getBiome(), climate));
        }
    }
    
    public static int getRawId(ResourceKey<Biome> key) {
        return BuiltinRegistries.BIOME.getId(BuiltinRegistries.BIOME.getOrThrow(key));
    }
    
    /**
     * Makes sure that the given registry key is mapped in {@link Biomes}. This mapping may be absent
     * if mods register their biomes only in {@link BuiltinRegistries#BIOME}, and not using the
     * private method in {@link Biomes}.
     */
    public static void ensureIdMapping(ResourceKey<Biome> biomeKey) {
        int rawId = getRawId(biomeKey);
        Int2ObjectMap<ResourceKey<Biome>> biomes = BiomesAccessor.getTO_NAME();
        
        if (!biomes.containsKey(rawId)) {
            Platform.LOGGER.debug("Automatically creating layer-related raw-id mapping for biome {}", biomeKey);
            biomes.put(rawId, biomeKey);
        }
    }
}