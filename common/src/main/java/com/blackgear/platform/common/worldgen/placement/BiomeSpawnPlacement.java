package com.blackgear.platform.common.worldgen.placement;

import com.blackgear.platform.common.worldgen.placement.parameters.Depth;
import com.blackgear.platform.common.worldgen.placement.parameters.Weirdness;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;

public class BiomeSpawnPlacement {
    public static final List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> BIOME_ENTRIES = Lists.newArrayList();

    public static void add(Pair<Climate.ParameterPoint, ResourceKey<Biome>> mapper) {
        BIOME_ENTRIES.add(mapper);
    }

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

    public static void addSurfaceBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        Climate.Parameter weirdness,
        float offset,
        ResourceKey<Biome> key
    ) {
        addBiome(temperature, humidity, continentalness, erosion, Depth.SURFACE.parameter(), weirdness, offset, key);
        addBiome(temperature, humidity, continentalness, erosion, Depth.FLOOR.parameter(), weirdness, offset, key);
    }

    public static void addUndergroundBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        Climate.Parameter weirdness,
        float offset,
        ResourceKey<Biome> key
    ) {
        addBiome(temperature, humidity, continentalness, erosion, Depth.UNDERGROUND.parameter(), weirdness, offset, key);
    }

    public static void addBottomBiome(
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        Climate.Parameter weirdness,
        float offset,
        ResourceKey<Biome> key
    ) {
        addBiome(temperature, humidity, continentalness, erosion, Depth.FLOOR.parameter(), weirdness, offset, key);
    }

    public static void addSurfaceBiome(
        Placement placement,
        Climate.Parameter temperature,
        Climate.Parameter humidity,
        Climate.Parameter continentalness,
        Climate.Parameter erosion,
        float offset,
        ResourceKey<Biome> key
    ) {
        for (Weirdness weirdness : placement.getWeirdnesses()) {
            addSurfaceBiome(temperature, humidity, continentalness, erosion, weirdness.parameter(), offset, key);
        }
    }
}