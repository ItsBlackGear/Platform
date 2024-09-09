package com.blackgear.platform.common.worldgen.modifier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;

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

    public abstract void addFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> feature);

    public abstract void removeFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> feature);

    public void replaceFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> replacement, ConfiguredFeature<?, ?> feature) {
        this.addFeature(decoration, replacement);
        this.removeFeature(decoration, feature);
    }
    
    public abstract void addStructure(ConfiguredStructureFeature<?, ?> structure);

    public abstract void removeStructure(ConfiguredStructureFeature<?, ?> structure);
    
    public void replaceStructure(ConfiguredStructureFeature<?, ?> replacement, ConfiguredStructureFeature<?, ?> structure) {
        this.addStructure(replacement);
        this.removeStructure(structure);
    }

    public abstract void addCarver(GenerationStep.Carving carving, ConfiguredWorldCarver<?> carver);

    public abstract void removeCarver(GenerationStep.Carving carving, ConfiguredWorldCarver<?> carver);
    
    public void replaceCarver(GenerationStep.Carving carving, ConfiguredWorldCarver<?> replacement, ConfiguredWorldCarver<?> carver) {
        this.addCarver(carving, replacement);
        this.removeCarver(carving, carver);
    }

    public abstract void addSurface(ConfiguredSurfaceBuilder<?> surface);

    public abstract void addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data);
    
    public abstract void removeSpawn(EntityType<?> entity);
    
    public void replaceSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
        this.removeSpawn(data.type);
        this.addSpawn(category, data);
    }
}