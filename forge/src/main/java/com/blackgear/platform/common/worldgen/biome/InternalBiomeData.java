package com.blackgear.platform.common.worldgen.biome;

import com.blackgear.platform.common.worldgen.parameters.Temperature;
import com.blackgear.platform.core.mixin.access.OverworldBiomeSourceAccessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.newbiome.layer.Layers;

import java.util.*;

public class InternalBiomeData {
    private static final EnumMap<Temperature, WeightedBiomePicker> OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS = new EnumMap<>(Temperature.class);
    private static final Map<ResourceKey<Biome>, WeightedBiomePicker> OVERWORLD_HILLS_MAP = new IdentityHashMap<>();
    private static final Map<ResourceKey<Biome>, WeightedBiomePicker> OVERWORLD_SHORE_MAP = new IdentityHashMap<>();
    private static final Map<ResourceKey<Biome>, WeightedBiomePicker> OVERWORLD_EDGE_MAP = new IdentityHashMap<>();
    private static final Map<ResourceKey<Biome>, VariantTransformer> OVERWORLD_VARIANT_TRANSFORMERS = new IdentityHashMap<>();
    private static final Map<ResourceKey<Biome>, ResourceKey<Biome>> OVERWORLD_RIVER_MAP = new IdentityHashMap<>();
    
    public static void addOverworldContinentalBiome(Temperature climate, ResourceKey<Biome> biome, double weight) {
        Preconditions.checkArgument(climate != null, "Climate is null");
        Preconditions.checkArgument(biome != null, "Biome is null");
        Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
        Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
        InternalBiomeUtils.ensureIdMapping(biome);
        OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS.computeIfAbsent(climate, k -> new WeightedBiomePicker()).addBiome(biome, weight);
        injectOverworldBiome(biome);
    }
    
    public static void addOverworldHillsBiome(ResourceKey<Biome> primary, ResourceKey<Biome> hills, double weight) {
        Preconditions.checkArgument(primary != null, "Primary biome is null");
        Preconditions.checkArgument(hills != null, "Hills biome is null");
        Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
        Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
        InternalBiomeUtils.ensureIdMapping(primary);
        InternalBiomeUtils.ensureIdMapping(hills);
        OVERWORLD_HILLS_MAP.computeIfAbsent(primary, biome -> DefaultHillsData.injectDefaultHills(primary, new WeightedBiomePicker())).addBiome(hills, weight);
        injectOverworldBiome(hills);
    }
    
    public static void addOverworldShoreBiome(ResourceKey<Biome> primary, ResourceKey<Biome> shore, double weight) {
        Preconditions.checkArgument(primary != null, "Primary biome is null");
        Preconditions.checkArgument(shore != null, "Shore biome is null");
        Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
        Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
        InternalBiomeUtils.ensureIdMapping(primary);
        InternalBiomeUtils.ensureIdMapping(shore);
        OVERWORLD_SHORE_MAP.computeIfAbsent(primary, biome -> new WeightedBiomePicker()).addBiome(shore, weight);
        injectOverworldBiome(shore);
    }
    
    public static void addOverworldEdgeBiome(ResourceKey<Biome> primary, ResourceKey<Biome> edge, double weight) {
        Preconditions.checkArgument(primary != null, "Primary biome is null");
        Preconditions.checkArgument(edge != null, "Edge biome is null");
        Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
        Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
        InternalBiomeUtils.ensureIdMapping(primary);
        InternalBiomeUtils.ensureIdMapping(edge);
        OVERWORLD_EDGE_MAP.computeIfAbsent(primary, biome -> new WeightedBiomePicker()).addBiome(edge, weight);
        injectOverworldBiome(edge);
    }
    
    public static void addOverworldBiomeReplacement(ResourceKey<Biome> replaced, ResourceKey<Biome> variant, double chance, Temperature[] climates) {
        Preconditions.checkArgument(replaced != null, "Replaced biome is null");
        Preconditions.checkArgument(variant != null, "Variant biome is null");
        Preconditions.checkArgument(chance > 0 && chance <= 1, "Chance is not greater than 0 or less than or equal to 1");
        InternalBiomeUtils.ensureIdMapping(replaced);
        InternalBiomeUtils.ensureIdMapping(variant);
        OVERWORLD_VARIANT_TRANSFORMERS.computeIfAbsent(replaced, biome -> new VariantTransformer()).addBiome(variant, chance, climates);
        injectOverworldBiome(variant);
    }
    
    public static void setOverworldRiverBiome(ResourceKey<Biome> primary, ResourceKey<Biome> river) {
        Preconditions.checkArgument(primary != null, "Primary biome is null");
        InternalBiomeUtils.ensureIdMapping(primary);
        InternalBiomeUtils.ensureIdMapping(river);
        OVERWORLD_RIVER_MAP.put(primary, river);
        
        if (river != null) {
            injectOverworldBiome(river);
        }
    }
    
    /**
     * Adds the biomes in world gen to the array for the vanilla layered biome source.
     * This helps with {@link OverworldBiomeSource#canGenerateStructure(StructureFeature)} returning correctly for modded biomes as well as in {@link OverworldBiomeSource#getSurfaceBlocks()}}
     */
    private static void injectOverworldBiome(ResourceKey<Biome> biome) {
        List<ResourceKey<Biome>> biomes = OverworldBiomeSourceAccessor.getPossibleBiomes();
        
        if (biomes instanceof ImmutableList) {
            biomes = new ArrayList<>(biomes);
            OverworldBiomeSourceAccessor.setPossibleBiomes(biomes);
        }
        
        biomes.add(biome);
    }
    
    public static Map<ResourceKey<Biome>, WeightedBiomePicker> getOverworldHills() {
        return OVERWORLD_HILLS_MAP;
    }
    
    public static Map<ResourceKey<Biome>, WeightedBiomePicker> getOverworldShores() {
        return OVERWORLD_SHORE_MAP;
    }
    
    public static Map<ResourceKey<Biome>, WeightedBiomePicker> getOverworldEdges() {
        return OVERWORLD_EDGE_MAP;
    }
    
    public static Map<ResourceKey<Biome>, ResourceKey<Biome>> getOverworldRivers() {
        return OVERWORLD_RIVER_MAP;
    }
    
    public static EnumMap<Temperature, WeightedBiomePicker> getOverworldModdedContinentalBiomePickers() {
        return OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS;
    }
    
    public static Map<ResourceKey<Biome>, VariantTransformer> getOverworldVariantTransformers() {
        return OVERWORLD_VARIANT_TRANSFORMERS;
    }
    
    private static class DefaultHillsData {
        private static final ImmutableMap<ResourceKey<Biome>, ResourceKey<Biome>> DEFAULT_HILLS;
        
        static WeightedBiomePicker injectDefaultHills(ResourceKey<Biome> base, WeightedBiomePicker picker) {
            ResourceKey<Biome> defaultHill = DEFAULT_HILLS.get(base);
            
            if (defaultHill != null) {
                picker.addBiome(defaultHill, 1);
            } else if (Layers.isSame(InternalBiomeUtils.getRawId(base), InternalBiomeUtils.getRawId(Biomes.WOODED_BADLANDS_PLATEAU))) {
                picker.addBiome(Biomes.BADLANDS, 1);
            } else if (base == Biomes.DEEP_OCEAN || base == Biomes.DEEP_LUKEWARM_OCEAN || base == Biomes.DEEP_COLD_OCEAN) {
                picker.addBiome(Biomes.PLAINS, 1);
                picker.addBiome(Biomes.FOREST, 1);
            } else if (base == Biomes.DEEP_FROZEN_OCEAN) {
                // Note: Vanilla Deep Frozen Oceans only have a 1/3 chance of having default hills.
                // This is a clever trick that ensures that when a mod adds hills with a weight of 1, the 1/3 chance is fulfilled.
                // 0.5 + 1.0 = 1.5, and 0.5 / 1.5 = 1/3.
                
                picker.addBiome(Biomes.PLAINS, 0.25);
                picker.addBiome(Biomes.FOREST, 0.25);
            } else if (base == Biomes.PLAINS) {
                picker.addBiome(Biomes.WOODED_HILLS, 1);
                picker.addBiome(Biomes.FOREST, 2);
            }
            
            return picker;
        }
        
        static {
            // This map mirrors the hardcoded logic in AddHillsLayer#sample
            ImmutableMap.Builder<ResourceKey<Biome>, ResourceKey<Biome>> builder = ImmutableMap.builder();
            builder.put(Biomes.DESERT, Biomes.DESERT_HILLS);
            builder.put(Biomes.FOREST, Biomes.WOODED_HILLS);
            builder.put(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS);
            builder.put(Biomes.DARK_FOREST, Biomes.PLAINS);
            builder.put(Biomes.TAIGA, Biomes.TAIGA_HILLS);
            builder.put(Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS);
            builder.put(Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS);
            builder.put(Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS);
            builder.put(Biomes.JUNGLE, Biomes.JUNGLE_HILLS);
            builder.put(Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS);
            builder.put(Biomes.OCEAN, Biomes.DEEP_OCEAN);
            builder.put(Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);
            builder.put(Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN);
            builder.put(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
            builder.put(Biomes.MOUNTAINS, Biomes.WOODED_MOUNTAINS);
            builder.put(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU);
            DEFAULT_HILLS = builder.build();
        }
    }
}