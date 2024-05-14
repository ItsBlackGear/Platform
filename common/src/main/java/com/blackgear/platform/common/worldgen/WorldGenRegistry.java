package com.blackgear.platform.common.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

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
    
    @SuppressWarnings("unchecked")
    private static <V extends T, T> Holder<V> register(Registry<T> registry, ResourceLocation location, V holder) {
        return (Holder<V>) BuiltinRegistries.register(registry, location, holder);
    }
    
    /**
     * Registers a Configured Feature
     */
    public <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> register(String key, F feature, FC configuration) {
        return register(
            BuiltinRegistries.CONFIGURED_FEATURE,
            new ResourceLocation(this.modId, key),
            new ConfiguredFeature<>(feature, configuration)
        );
    }
    
    /**
     * Registers a Placed Feature
     */
    public Holder<PlacedFeature> register(String key, Holder<? extends ConfiguredFeature<?, ?>> feature, PlacementModifier... placements) {
        return register(
            BuiltinRegistries.PLACED_FEATURE,
            new ResourceLocation(this.modId, key),
            new PlacedFeature(Holder.hackyErase(feature), List.of(placements))
        );
    }
    
    /**
     * Registers a Placed Feature
     */
    public Holder<PlacedFeature> register(String key, Holder<? extends ConfiguredFeature<?, ?>> feature, List<PlacementModifier> placements) {
        return register(
            BuiltinRegistries.PLACED_FEATURE,
            new ResourceLocation(this.modId, key),
            new PlacedFeature(Holder.hackyErase(feature), List.copyOf(placements))
        );
    }
    
    public void register() {}
}