package com.blackgear.platform.common.worldgen;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

/**
 * Utility class, similar to the CoreRegistry, designed to register world generation features.
 * @author ItsBlackGear
 **/
public class WorldGenRegistry {
    protected final String modId;

    private WorldGenRegistry(String modId) {
        this.modId = modId;
    }

    /**
     * Creates a new instance of the WorldGenRegistry.
     */
    public static WorldGenRegistry create(String modId) {
        return new WorldGenRegistry(modId);
    }

    private static <V extends T, T> V register(Registry<T> registry, ResourceLocation location, V holder) {
        return BuiltinRegistries.register(registry, location, holder);
    }
    
    /**
     * Registers a Configured Feature
     */
    public <FC extends FeatureConfiguration> ConfiguredFeature<FC, ?> feature(String key, ConfiguredFeature<FC, ?> feature) {
        return register(
            BuiltinRegistries.CONFIGURED_FEATURE,
            new ResourceLocation(this.modId, key),
            feature
        );
    }

    /**
     * Registers a Configured Structure
     */
    public <FC extends FeatureConfiguration> ConfiguredStructureFeature<FC, ?> structure(String key, ConfiguredStructureFeature<FC, ?> structure) {
        return register(
            BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE,
            new ResourceLocation(this.modId, key),
            structure
        );
    }

    /**
     * Registers a Configured Carver
     */
    public <FC extends CarverConfiguration> ConfiguredWorldCarver<FC> carver(String key, ConfiguredWorldCarver<FC> carver) {
        return register(
            BuiltinRegistries.CONFIGURED_CARVER,
            new ResourceLocation(this.modId, key),
            carver
        );
    }

    /**
     * Registers a Configured Surface
     */
    public <FC extends SurfaceBuilderConfiguration> ConfiguredSurfaceBuilder<FC> surface(String key, ConfiguredSurfaceBuilder<FC> surface) {
        return register(
            BuiltinRegistries.CONFIGURED_SURFACE_BUILDER,
            new ResourceLocation(this.modId, key),
            surface
        );
    }

    public void register() {}
}