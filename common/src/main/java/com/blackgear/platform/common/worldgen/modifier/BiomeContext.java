package com.blackgear.platform.common.worldgen.modifier;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class designed to identify the biome or biomes for implementing biome-specific features.
 **/
public interface BiomeContext {
    Predicate<BiomeContext> OVERWORLD_BIOME = context -> MultiNoiseBiomeSource.Preset.OVERWORLD.possibleBiomes().anyMatch(context::is);
    
    ResourceKey<Biome> resource();
    
    Biome biome();
    
    boolean is(TagKey<Biome> category);
    
    boolean is(ResourceKey<Biome> biome);
    
    boolean is(Predicate<BiomeContext> context);

    boolean hasFeature(Holder<PlacedFeature> feature);

    default boolean hasEntity(Supplier<EntityType<?>> entities) {
        return hasEntity(ImmutableSet.of(entities));
    }
    
    default boolean hasEntity(Set<Supplier<EntityType<?>>> entitySet) {
        Set<EntityType<?>> entities = entitySet.stream()
            .map(Supplier::get)
            .collect(Collectors.toSet());
        
        MobSpawnSettings settings = this.biome().getMobSettings();
        
        return Arrays.stream(MobCategory.values())
            .flatMap(category -> settings.getMobs(category).unwrap().stream())
            .anyMatch(spawner -> entities.contains(spawner.type));
    }
}