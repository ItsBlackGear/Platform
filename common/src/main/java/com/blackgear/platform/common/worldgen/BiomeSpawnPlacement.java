package com.blackgear.platform.common.worldgen;

import com.blackgear.platform.common.worldgen.parameters.Temperature;
import com.blackgear.platform.core.mixin.access.BiomesAccessor;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;
import java.util.function.Predicate;

public class BiomeSpawnPlacement {
    private static final Map<ResourceKey<Biome>, ResourceKey<Biome>> OVERWORLD_BORDER_MAP = new HashMap<>();
    private static final Map<ResourceKey<Biome>, List<PredicatedBiomeEntry>> PREDICATED_BORDER_MAP = new HashMap<>();
    
    private static ResourceKey<Biome> sanitize(ResourceKey<Biome> biome) {
        int rawId = BuiltinRegistries.BIOME.getId(BuiltinRegistries.BIOME.get(biome));
        BiomesAccessor.getTO_NAME().computeIfAbsent(rawId, key -> biome);
        return biome;
    }
    
    public static void addBorderBiome(ResourceKey<Biome> biome, ResourceKey<Biome> border) {
        OVERWORLD_BORDER_MAP.put(sanitize(biome), sanitize(border));
    }
    
    public static Optional<ResourceKey<Biome>> getBorder(ResourceKey<Biome> biome) {
        return Optional.ofNullable(OVERWORLD_BORDER_MAP.get(biome));
    }
    
    public static List<PredicatedBiomeEntry> getPredicatedBorders(ResourceKey<Biome> biome) {
        return PREDICATED_BORDER_MAP.getOrDefault(biome, new ArrayList<>());
    }
    
    public static void addPredicatedBorderBiome(ResourceKey<Biome> biomeBase, ResourceKey<Biome> biomeBorder, Predicate<ResourceKey<Biome>> predicate) {
        PREDICATED_BORDER_MAP.computeIfAbsent(sanitize(biomeBase), biome -> new ArrayList<>()).add(new PredicatedBiomeEntry(sanitize(biomeBorder), predicate));
    }
    
    public static final class PredicatedBiomeEntry {
        public final ResourceKey<Biome> biome;
        public final Predicate<ResourceKey<Biome>> predicate;
        
        PredicatedBiomeEntry(ResourceKey<Biome> biome, Predicate<ResourceKey<Biome>> predicate) {
            this.biome = biome;
            this.predicate = predicate;
        }
    }
    
    @ExpectPlatform
    public static void addContinentalBiome(ResourceKey<Biome> biome, Temperature temperature, double weight) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void addHillsBiome(ResourceKey<Biome> parent, ResourceKey<Biome> hills, double weight) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void addShoreBiome(ResourceKey<Biome> parent, ResourceKey<Biome> shore, double weight) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void addEdgeBiome(ResourceKey<Biome> parent, ResourceKey<Biome> edge, double weight) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void addBiomeVariant(ResourceKey<Biome> replaced, ResourceKey<Biome> variant, double chance, Temperature... temperatures) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void addRiverBiome(ResourceKey<Biome> parent, ResourceKey<Biome> river) {
        throw new AssertionError();
    }
}