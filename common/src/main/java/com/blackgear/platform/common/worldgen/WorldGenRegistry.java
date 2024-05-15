package com.blackgear.platform.common.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;

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
  
    public <T> void register(BootstapContext<T> context, ResourceKey<T> key, T entry) {
        context.register(key, entry);
    }
    
    /**
     * Registers a Configured Feature
     */
    public <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
        BootstapContext<ConfiguredFeature<?, ?>> context,
        ResourceKey<ConfiguredFeature<?, ?>> key,
        F feature,
        FC configuration
    ) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
    
    /**
     * Registers a Configured Feature without custom configuration.
     */
    public void register(
        BootstapContext<ConfiguredFeature<?, ?>> context,
        ResourceKey<ConfiguredFeature<?, ?>> key,
        Feature<NoneFeatureConfiguration> feature
    ) {
        this.register(context, key, feature, FeatureConfiguration.NONE);
    }
    
    /**
     * Registers a Placed Feature
     */
    
    public void register(
        BootstapContext<PlacedFeature> context,
        ResourceKey<PlacedFeature> key,
        Holder<ConfiguredFeature<?, ?>> feature,
        List<PlacementModifier> placements
    ) {
        context.register(key, new PlacedFeature(feature, List.copyOf(placements)));
    }
    
    /**
     * Registers a Placed Feature
     */
    public void register(
        BootstapContext<PlacedFeature> context,
        ResourceKey<PlacedFeature> key,
        Holder<ConfiguredFeature<?, ?>> feature,
        PlacementModifier... placements
    ) {
        this.register(context, key, feature, List.of(placements));
    }
    
    /**
     * Registers Noise Parameters
     */
    public void register(
        BootstapContext<NormalNoise.NoiseParameters> context,
        ResourceKey<NormalNoise.NoiseParameters> key,
        int firstOctave,
        double firstAmplitude,
        double... amplitudes
    ) {
        context.register(key, new NormalNoise.NoiseParameters(firstOctave, firstAmplitude, amplitudes));
    }
    
    public void register() {}
}