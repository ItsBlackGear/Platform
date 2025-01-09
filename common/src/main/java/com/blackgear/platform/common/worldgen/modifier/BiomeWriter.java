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

    public void addFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> feature, boolean condition) {
        if (condition) {
            this.addFeature(decoration, feature);
        }
    }

    public void removeFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> feature, boolean condition) {
        if (condition) {
            this.removeFeature(decoration, feature);
        }
    }

    public abstract void addFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> feature);

    public abstract void removeFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> feature);

    public void replaceFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> replacement, ConfiguredFeature<?, ?> feature) {
        if (this.context().hasFeature(feature)) {
            this.addFeature(decoration, replacement);
            this.removeFeature(decoration, feature);
        }
    }

    public void addStructure(ConfiguredStructureFeature<?, ?> structure, boolean condition) {
        if (condition) {
            this.addStructure(structure);
        }
    }

    public void removeStructure(ConfiguredStructureFeature<?, ?> structure, boolean condition) {
        if (condition) {
            this.removeStructure(structure);
        }
    }

    public abstract void addStructure(ConfiguredStructureFeature<?, ?> structure);

    public abstract void removeStructure(ConfiguredStructureFeature<?, ?> structure);

    public void addCarver(GenerationStep.Carving carving, ConfiguredWorldCarver<?> carver, boolean condition) {
        if (condition) {
            this.addCarver(carving, carver);
        }
    }

    public void removeCarver(GenerationStep.Carving carving, ConfiguredWorldCarver<?> carver, boolean condition) {
        if (condition) {
            this.removeCarver(carving, carver);
        }
    }

    public abstract void addCarver(GenerationStep.Carving carving, ConfiguredWorldCarver<?> carver);

    public abstract void removeCarver(GenerationStep.Carving carving, ConfiguredWorldCarver<?> carver);

    public abstract void addSurface(ConfiguredSurfaceBuilder<?> surface);

    public void addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data, boolean condition) {
        if (condition) {
            this.addSpawn(category, data);
        }
    }

    public void removeSpawn(EntityType<?> entity, boolean condition) {
        if (condition) {
            this.removeSpawn(entity);
        }
    }

    public abstract void addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data);
    
    public abstract void removeSpawn(EntityType<?> entity);
    
    public void replaceSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
        if (this.context().hasEntity(() -> data.type)) {
            this.removeSpawn(data.type);
            this.addSpawn(category, data);
        }
    }
}