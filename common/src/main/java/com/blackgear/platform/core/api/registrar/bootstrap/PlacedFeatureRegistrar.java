package com.blackgear.platform.core.api.registrar.bootstrap;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class PlacedFeatureRegistrar extends BootstrapRegistrar<PlacedFeature> {
    private PlacedFeatureRegistrar(ResourceKey<? extends Registry<PlacedFeature>> registry, String modId) {
        super(registry, modId);
    }

    public static PlacedFeatureRegistrar create(String modId) {
        return new PlacedFeatureRegistrar(Registries.PLACED_FEATURE, modId);
    }

    public ResourceKey<PlacedFeature> register(
        String name,
        Factory factory
    ) {
        return this.register(name, context -> factory.create(context.lookup(Registries.CONFIGURED_FEATURE), context.lookup(Registries.PLACED_FEATURE)));
    }

    public ResourceKey<PlacedFeature> register(
        String name,
        ResourceKey<ConfiguredFeature<?, ?>> feature,
        List<PlacementModifier> placements
    ) {
        return this.register(name, (configured, placed) -> new PlacedFeature(configured.getOrThrow(feature), placements));
    }

    public ResourceKey<PlacedFeature> register(
        String name,
        ResourceKey<ConfiguredFeature<?, ?>> feature,
        PlacementModifier... placements
    ) {
        return this.register(name, (configured, placed) -> new PlacedFeature(configured.getOrThrow(feature), List.of(placements)));
    }

    @FunctionalInterface
    public interface Factory {
        PlacedFeature create(HolderGetter<ConfiguredFeature<?, ?>> configured, HolderGetter<PlacedFeature> placed);
    }
}