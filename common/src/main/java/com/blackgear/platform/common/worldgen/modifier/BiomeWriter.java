package com.blackgear.platform.common.worldgen.modifier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.BiConsumer;

/**
 * Utility class to apply modifications to biomes.
 **/
public abstract class BiomeWriter {
    public void add(BiConsumer<BiomeWriter, BiomeContext> modifier) {
        modifier.accept(this, this.context());
    }

    public abstract ResourceLocation name();

    public abstract BiomeContext context();

    public abstract void addFeature(GenerationStep.Decoration decoration, ResourceKey<PlacedFeature> feature);

    public abstract void addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data);

    public abstract void addCarver(GenerationStep.Carving carving, ResourceKey<ConfiguredWorldCarver<?>> carver);
}