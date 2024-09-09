package com.blackgear.platform.common.registry;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.worldgen.feature.*;
import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.function.Supplier;

public class PlatformFeatures {
    public static final CoreRegistry<Feature<?>> FEATURES = CoreRegistry.create(Registry.FEATURE, Platform.MOD_ID);
    
    public static final Supplier<Feature<SingleBlockConfiguration>> SINGLE_BLOCK = FEATURES.register(
        "single_block",
        () -> new SingleBlockFeature(SingleBlockConfiguration.CODEC)
    );
    public static final Supplier<Feature<GrowingPlantConfiguration>> GROWING_PLANT = FEATURES.register(
        "growing_plant",
        () -> new GrowingPlantFeature(GrowingPlantConfiguration.CODEC)
    );
    public static final Supplier<Feature<OverlayOreConfiguration>> OVERLAY_ORE = FEATURES.register(
        "overlay_ore",
        () -> new OverlayOreFeature(OverlayOreConfiguration.CODEC)
    );
    public static final Supplier<Feature<MultifaceGrowthConfiguration>> MULTIFACE_GROWTH = FEATURES.register(
        "multiface_growth",
        () -> new MultifaceGrowthFeature(MultifaceGrowthConfiguration.CODEC)
    );
    public static final Supplier<Feature<VegetationPatchConfiguration>> VEGETATION_PATCH = FEATURES.register(
        "vegetation_patch",
        () -> new VegetationPatchFeature(VegetationPatchConfiguration.CODEC)
    );
    public static final Supplier<Feature<VegetationPatchConfiguration>> WATERLOGGED_VEGETATION_PATCH = FEATURES.register(
        "waterlogged_vegetation_patch",
        () -> new WaterloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC)
    );
}