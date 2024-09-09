package com.blackgear.platform.common;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.providers.height.TrapezoidHeight;
import com.blackgear.platform.common.providers.height.UniformHeight;
import com.blackgear.platform.common.providers.height.VeryBiasedToBottomHeight;
import com.blackgear.platform.common.worldgen.WorldGenRegistry;
import com.blackgear.platform.common.worldgen.decorator.CountConfiguration;
import com.blackgear.platform.common.worldgen.decorator.RangedConfiguration;
import com.blackgear.platform.common.worldgen.height.VerticalAnchor;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;

public class UndergroundFeatures {
    public static final WorldGenRegistry FEATURES = WorldGenRegistry.create(Platform.MOD_ID);
    
    public static final ConfiguredFeature<?, ?> ORE_TUFF = FEATURES.feature(
        "ore_tuff",
//        PlatformFeatures.OVERLAY_ORE.get()
//            .configured(
//                new OverlayOreConfiguration(
//                    OreConfiguration.Predicates.NATURAL_STONE,
//                    Blocks.BLACKSTONE.defaultBlockState(),
//                    64
//                )
//            )
        Feature.ORE
            .configured(
                new OreConfiguration(
                    OreConfiguration.Predicates.NATURAL_STONE,
                    Blocks.BLACKSTONE.defaultBlockState(),
                    64
                )
            )
            .decorated(RangedConfiguration.of(UniformHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(0))))
            .squared()
            .decorated(CountConfiguration.of(2))
    );
    public static final ConfiguredFeature<?, ?> ORE_DIAMOND = FEATURES.feature(
        "ore_diamond",
        Feature.ORE
            .configured(
                new OreConfiguration(
                    OreConfiguration.Predicates.NATURAL_STONE,
                    Blocks.DIAMOND_ORE.defaultBlockState(),
                    4
                )
            )
            .decorated(RangedConfiguration.of(TrapezoidHeight.of(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))))
            .squared()
            .decorated(CountConfiguration.of(7))
    );
    public static final ConfiguredFeature<?, ?> ORE_GOLD = FEATURES.feature(
        "ore_gold",
        Feature.ORE
            .configured(
                new OreConfiguration(
                    OreConfiguration.Predicates.NATURAL_STONE,
                    Blocks.GOLD_BLOCK.defaultBlockState(),
                    9
                )
            )
            .decorated(RangedConfiguration.of(TrapezoidHeight.of(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))))
            .squared()
            .decorated(CountConfiguration.of(4))
    );
    public static final ConfiguredFeature<?, ?> ORE_DIAMOND_LARGE = FEATURES.feature(
        "ore_diamond_large",
        Feature.ORE
            .configured(
                new OreConfiguration(
                    OreConfiguration.Predicates.NATURAL_STONE,
                    Blocks.DIAMOND_BLOCK.defaultBlockState(),
                    12
                )
            )
            .decorated(RangedConfiguration.of(TrapezoidHeight.of(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))))
            .squared()
            .decorated(CountConfiguration.of(9))
    );
    public static final ConfiguredFeature<?, ?> SPRING_LAVA = FEATURES.feature(
        "spring_lava",
        Feature.SPRING
            .configured(
                new SpringConfiguration(
                    Blocks.LAVA.defaultBlockState().getFluidState(),
                    true,
                    4,
                    1,
                    ImmutableSet.of(
                        Blocks.STONE,
                        Blocks.GRANITE,
                        Blocks.DIORITE,
                        Blocks.ANDESITE,
                        Blocks.BLACKSTONE
                    )
                )
            )
            .decorated(RangedConfiguration.of(VeryBiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.belowTop(8), 8)))
            .squared()
            .decorated(CountConfiguration.of(20))
    );
    public static final ConfiguredFeature<?, ?> SPRING_WATER = FEATURES.feature(
        "spring_water",
        Feature.SPRING
            .configured(
                new SpringConfiguration(
                    Blocks.WATER.defaultBlockState().getFluidState(),
                    true,
                    4,
                    1,
                    ImmutableSet.of(
                        Blocks.STONE,
                        Blocks.GRANITE,
                        Blocks.DIORITE,
                        Blocks.ANDESITE,
                        Blocks.BLACKSTONE,
                        Blocks.DIRT,
                        Blocks.SNOW_BLOCK,
                        Blocks.PACKED_ICE
                    )
                )
            )
            .decorated(RangedConfiguration.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()))
            .squared()
            .decorated(CountConfiguration.of(40))
    );
}