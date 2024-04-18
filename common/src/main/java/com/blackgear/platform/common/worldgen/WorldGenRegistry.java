package com.blackgear.platform.common.worldgen;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Utility class, similar to the CoreRegistry, designed to register world generation features.
 *
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
    public <FC extends FeatureConfiguration, F extends Feature<FC>> ConfiguredFeature<FC, ?> register(String key, F feature, FC configuration) {
        return register(
            BuiltinRegistries.CONFIGURED_FEATURE,
            new ResourceLocation(this.modId, key),
            new ConfiguredFeature<>(feature, configuration)
        );
    }

    /**
     * Registers a Configured Feature
     */
    public <FC extends FeatureConfiguration, F extends Feature<FC>> ConfiguredFeature<FC, ?> register(String key, ConfiguredFeature<FC, ?> feature) {
        return register(
            BuiltinRegistries.CONFIGURED_FEATURE,
            new ResourceLocation(this.modId, key),
            feature
        );
    }

    public void register() {}
}