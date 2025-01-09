package com.blackgear.platform.common.worldgen.forge;

import com.blackgear.platform.common.worldgen.biome.InternalBiomeData;
import com.blackgear.platform.common.worldgen.parameters.Temperature;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeManager;

public class BiomeSpawnPlacementImpl {
    public static void addContinentalBiome(ResourceKey<Biome> biome, Temperature temperature, double weight) {
        InternalBiomeData.addOverworldContinentalBiome(temperature, biome, weight);
    }
    
    public static void addHillsBiome(ResourceKey<Biome> parent, ResourceKey<Biome> hills, double weight) {
        InternalBiomeData.addOverworldHillsBiome(parent, hills, weight);
    }
    
    public static void addShoreBiome(ResourceKey<Biome> parent, ResourceKey<Biome> shore, double weight) {
        InternalBiomeData.addOverworldShoreBiome(parent, shore, weight);
    }
    
    public static void addEdgeBiome(ResourceKey<Biome> parent, ResourceKey<Biome> edge, double weight) {
        InternalBiomeData.addOverworldEdgeBiome(parent, edge, weight);
    }
    
    public static void addBiomeVariant(ResourceKey<Biome> replaced, ResourceKey<Biome> variant, double chance, Temperature... temperatures) {
        InternalBiomeData.addOverworldBiomeReplacement(replaced, variant, chance, temperatures);
    }
    
    public static void addRiverBiome(ResourceKey<Biome> parent, ResourceKey<Biome> river) {
        InternalBiomeData.setOverworldRiverBiome(parent, river);
    }
}