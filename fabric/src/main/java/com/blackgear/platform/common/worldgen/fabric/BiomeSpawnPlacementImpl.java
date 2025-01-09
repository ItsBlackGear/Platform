package com.blackgear.platform.common.worldgen.fabric;

import com.blackgear.platform.common.worldgen.parameters.Temperature;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeSpawnPlacementImpl {
    public static void addContinentalBiome(ResourceKey<Biome> biome, Temperature temperature, double weight) {
        OverworldClimate type = getType(temperature);
        OverworldBiomes.addContinentalBiome(biome, type, weight);
    }
    
    public static void addHillsBiome(ResourceKey<Biome> parent, ResourceKey<Biome> hills, double weight) {
        OverworldBiomes.addHillsBiome(parent, hills, weight);
    }
    
    public static void addShoreBiome(ResourceKey<Biome> parent, ResourceKey<Biome> shore, double weight) {
        OverworldBiomes.addShoreBiome(parent, shore, weight);
    }
    
    public static void addEdgeBiome(ResourceKey<Biome> parent, ResourceKey<Biome> edge, double weight) {
        OverworldBiomes.addEdgeBiome(parent, edge, weight);
    }
    
    public static void addBiomeVariant(ResourceKey<Biome> replaced, ResourceKey<Biome> variant, double chance, Temperature... temperatures) {
        for (Temperature temperature : temperatures) {
            OverworldBiomes.addBiomeVariant(replaced, variant, chance, getType(temperature));
        }
    }
    
    public static void addRiverBiome(ResourceKey<Biome> parent, ResourceKey<Biome> river) {
        OverworldBiomes.setRiverBiome(parent, river);
    }
    
    private static OverworldClimate getType(Temperature temperature) {
        switch (temperature) {
            case ICY:
                return OverworldClimate.SNOWY;
            case COOL:
                return OverworldClimate.COOL;
            case WARM:
                return OverworldClimate.TEMPERATE;
            default:
                return OverworldClimate.DRY;
        }
    }
}