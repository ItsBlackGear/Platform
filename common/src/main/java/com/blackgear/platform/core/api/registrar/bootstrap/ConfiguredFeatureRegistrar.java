package com.blackgear.platform.core.api.registrar.bootstrap;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ConfiguredFeatureRegistrar extends BootstrapRegistrar<ConfiguredFeature<?, ?>> {
    private ConfiguredFeatureRegistrar(ResourceKey<? extends Registry<ConfiguredFeature<?, ?>>> registry, String modId) {
        super(registry, modId);
    }

    public static ConfiguredFeatureRegistrar create(String modId) {
        return new ConfiguredFeatureRegistrar(Registries.CONFIGURED_FEATURE, modId);
    }

    public <FC extends FeatureConfiguration, F extends Feature<FC>> ResourceKey<ConfiguredFeature<?, ?>> register(
        String name,
        F feature,
        Factory<FC> configuration
    ) {
        return this.register(name, context -> new ConfiguredFeature<>(feature, configuration.create(context.lookup(Registries.CONFIGURED_FEATURE), context.lookup(Registries.PLACED_FEATURE))));
    }

    public ResourceKey<ConfiguredFeature<?, ?>> register(
        String name,
        Feature<NoneFeatureConfiguration> feature
    ) {
        return this.register(name, context -> new ConfiguredFeature<>(feature, FeatureConfiguration.NONE));
    }

    @FunctionalInterface
    public interface Factory<FC extends FeatureConfiguration> {
        FC create(HolderGetter<ConfiguredFeature<?, ?>> configured, HolderGetter<PlacedFeature> placed);
    }
}