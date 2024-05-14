package com.blackgear.platform.common.worldgen.placement;

import com.blackgear.platform.common.worldgen.placement.parameters.Depth;
import com.blackgear.platform.common.worldgen.placement.parameters.Temperatures;
import com.blackgear.platform.common.worldgen.placement.parameters.Weirdness;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;

/**
 * Utility class to add custom biome spawn placements into the world.
 * <p>For load time purposes, it's highly recommended to use this only at the post-common setup of your ModInstance</p>
 *
 * @see net.minecraft.world.level.biome.OverworldBiomeBuilder
 * @apiNote This does not replace Terrablender, for better compatibility with other mods it's still the best option.
 *
 * @author ItsBlackGear
 */
public class BiomeSpawnPlacement {
    public static final List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> BIOME_ENTRIES = Lists.newArrayList();
    public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
    public static final Climate.Parameter FROZEN_RANGE = Temperatures.ICY.point();
    public static final Climate.Parameter UNFROZEN_RANGE = Temperatures.span(Temperatures.COOL, Temperatures.HOT);
    
    private static void add(Pair<Climate.ParameterPoint, ResourceKey<Biome>> mapper) {
        BIOME_ENTRIES.add(mapper);
    }
    
    /**
     * Register a custom biome placement without any presets for generation.
     *
     * <p>Example of creating a custom biome placement:</p>
     *
     * <pre> {@code
     *
     * BiomeSpawnPlacement.addBiome(
     *     // Temperature
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Humidity
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Continentalness
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Erosion
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Depth
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Weirdness
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Offset
     *     0.0F,
     *     // Biome
     *     Biomes.PLAINS
     * );
     *
     * } </pre>
     *
     * The example adds the plains biome in the entire world.
     */
    public static void addBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        Climate.Parameter depth,
        Climate.Parameter weirdness,
        float offset,
        ResourceKey<Biome> key
    ) {
        add(
            Pair.of(
                Climate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, offset),
                key
            )
        );
    }
    
    /**
     * Register a surface biome placement without a specific Weirdness preset.
     *
     * <p>Example of creating a surface biome placement:</p>
     *
     * <pre> {@code
     *
     * BiomeSpawnPlacement.addSurfaceBiome(
     *     // Temperature
     *     BiomeSpawnPlacement.UNFROZEN_RANGE,
     *     // Humidity
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Continentalness
     *     Continentalness.COAST.point(),
     *     // Erosion
     *     Erosions.span(Erosions.EROSION_0, Erosions.EROSION_1),
     *     // Weirdness
     *     Weirdness.VALLEY,
     *     // Offset
     *     0.0F,
     *     // Biome
     *     Biomes.RIVER
     * );
     *
     * } </pre>
     *
     * The example adds the river biome into the world.
     */
    public static void addSurfaceBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        Climate.Parameter weirdness,
        float offset,
        ResourceKey<Biome> key
    ) {
        addBiome(temperature, humidity, continentalness, erosion, Depth.SURFACE.point(), weirdness, offset, key);
        addBiome(temperature, humidity, continentalness, erosion, Depth.FLOOR.point(), weirdness, offset, key);
    }
    
    /**
     * Register a custom underground biome placement.
     *
     * <p>Example of creating an underground biome placement:</p>
     *
     * <pre> {@code
     *
     * BiomeSpawnPlacement.addUndergroundBiome(
     *     // Temperature
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Humidity
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Continentalness
     *     Climate.Parameter.span(0.8F, 1.0F),
     *     // Erosion
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Weirdness
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Offset
     *     0.0F,
     *     // Biome
     *     Biomes.DRIPSTONE_CAVES
     * );
     * BiomeSpawnPlacement.addUndergroundBiome(
     *     // Temperature
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Humidity
     *     Climate.Parameter.span(0.7F, 1.0F),
     *     // Continentalness
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Erosion
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Weirdness
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Offset
     *     0.0F,
     *     // Biome
     *     Biomes.LUSH_CAVES
     * );
     *
     * } </pre>
     *
     * The example adds the dripstone caves and lush caves biomes into the overworld.
     */
    public static void addUndergroundBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        Climate.Parameter weirdness,
        float offset,
        ResourceKey<Biome> key
    ) {
        addBiome(temperature, humidity, continentalness, erosion, Depth.UNDERGROUND.point(), weirdness, offset, key);
    }
    
    /**
     * Register a custom underground biome placement that only generates at the depths of the world.
     *
     * <p>Example of creating a bottom biome placement:</p>
     *
     * <pre> {@code
     *
     * BiomeSpawnPlacement.addBottomBiome(
     *     // Temperature
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Humidity
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Continentalness
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Erosion
     *     Erosions.span(Erosions.EROSION_0, Erosions.EROSION_1),
     *     // Weirdness
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Offset
     *     0.0F,
     *     // Biome
     *     Biomes.DEEP_DARK
     * );
     *
     * } </pre>
     *
     * The example adds the deep dark biome into the overworld.
     */
    public static void addBottomBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        Climate.Parameter weirdness,
        float offset,
        ResourceKey<Biome> key
    ) {
        addBiome(temperature, humidity, continentalness, erosion, Depth.UNDERGROUND.point(), weirdness, offset, key);
    }
    
    /**
     * Register a custom surface biome placement with a defined placement preset.
     *
     * <p>Example of creating a surface biome placement:</p>
     *
     * <pre> {@code
     *
     * BiomeSpawnPlacement.addSurfaceBiome(
     *     // Placement
     *     Placement.VALLEY,
     *     // Temperature
     *     Temperatures.span(Temperatures.WARM, Temperatures.HOT),
     *     // Humidity
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Continentalness
     *     Continentalness.span(Continentalness.INLAND, Continentalness.FAR_INLAND),
     *     // Erosion
     *     Erosions.EROSION_6,
     *     // Offset
     *     0.0F,
     *     // Biome
     *     Biomes.MANGROVE_SWAMP
     * );
     * BiomeSpawnPlacement.addSurfaceBiome(
     *     // Placement
     *     Placement.LOW_SLICE,
     *     // Temperature
     *     Temperatures.span(Temperatures.WARM, Temperatures.HOT),
     *     // Humidity
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Continentalness
     *     Continentalness.span(Continentalness.INLAND, Continentalness.FAR_INLAND),
     *     // Erosion
     *     Erosions.EROSION_6,
     *     // Offset
     *     0.0F,
     *     // Biome
     *     Biomes.MANGROVE_SWAMP
     * );
     * BiomeSpawnPlacement.addSurfaceBiome(
     *     // Placement
     *     Placement.MID_SLICE,
     *     // Temperature
     *     Temperatures.span(Temperatures.WARM, Temperatures.HOT),
     *     // Humidity
     *     BiomeSpawnPlacement.FULL_RANGE,
     *     // Continentalness
     *     Continentalness.span(Continentalness.INLAND, Continentalness.FAR_INLAND),
     *     // Erosion
     *     Erosions.EROSION_6,
     *     // Offset
     *     0.0F,
     *     // Biome
     *     Biomes.MANGROVE_SWAMP
     * );
     *
     * } </pre>
     *
     * The example adds the mangrove swamp into the overworld at Valley, Low-Slice, and Mid-Slice.
     */
    public static void addSurfaceBiome(
        Placement placement,
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        float offset,
        ResourceKey<Biome> key
    ) {
        switch (placement) {
            case VALLEY -> addValleyBiome(temperature, humidity, continentalness, erosion, offset, key);
            case LOW_SLICE -> addLowSliceBiome(temperature, humidity, continentalness, erosion, offset, key);
            case MID_SLICE -> addMidSliceBiome(temperature, humidity, continentalness, erosion, offset, key);
            case HIGH_SLICE -> addHighSliceBiome(temperature, humidity, continentalness, erosion, offset, key);
            case PEAK -> addPeakBiome(temperature, humidity, continentalness, erosion, offset, key);
        }
    }
    
    // ========== Surface Biome Placement Presets ==========
    
    private static void addValleyBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        float offset,
        ResourceKey<Biome> key
    ) {
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.VALLEY.point(), offset, key);
    }
    
    private static void addLowSliceBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        float offset,
        ResourceKey<Biome> key
    ) {
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.LOW_SLICE_NORMAL_DESCENDING.point(), offset, key);
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.LOW_SLICE_VARIANT_ASCENDING.point(), offset, key);
    }
    
    private static void addMidSliceBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        float offset,
        ResourceKey<Biome> key
    ) {
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.MID_SLICE_NORMAL_ASCENDING.point(), offset, key);
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.MID_SLICE_NORMAL_DESCENDING.point(), offset, key);
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.MID_SLICE_VARIANT_ASCENDING.point(), offset, key);
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.MID_SLICE_VARIANT_DESCENDING.point(), offset, key);
    }
    
    private static void addHighSliceBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        float offset,
        ResourceKey<Biome> key
    ) {
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.HIGH_SLICE_NORMAL_ASCENDING.point(), offset, key);
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.HIGH_SLICE_NORMAL_DESCENDING.point(), offset, key);
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.HIGH_SLICE_VARIANT_ASCENDING.point(), offset, key);
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.HIGH_SLICE_VARIANT_DESCENDING.point(), offset, key);
    }
    
    private static void addPeakBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        float offset,
        ResourceKey<Biome> key
    ) {
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.PEAK_NORMAL.point(), offset, key);
        addSurfaceBiome(temperature, humidity, continentalness, erosion, Weirdness.PEAK_VARIANT.point(), offset, key);
    }
}