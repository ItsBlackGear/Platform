package com.blackgear.platform.common.registry;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.worldgen.decorator.*;
import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import java.util.function.Supplier;

public class PlatformDecorators {
    public static final CoreRegistry<FeatureDecorator<?>> DECORATOR = CoreRegistry.create(Registry.DECORATOR, Platform.MOD_ID);
    
    public static final Supplier<FeatureDecorator<CountConfiguration>> COUNT = DECORATOR.register(
        "count",
        () -> new CountDecorator(CountConfiguration.CODEC)
    );
    public static final Supplier<FeatureDecorator<RangedConfiguration>> RANGE = DECORATOR.register(
        "range",
        () -> new RangedDecorator(RangedConfiguration.CODEC)
    );
    public static final Supplier<FeatureDecorator<RandomOffsetConfiguration>> RANDOM_OFFSET = DECORATOR.register(
        "random_offset",
        () -> new RandomOffsetDecorator(RandomOffsetConfiguration.CODEC)
    );
    public static final Supplier<FeatureDecorator<CaveSurfaceConfiguration>> CAVE_SURFACE = DECORATOR.register(
        "cave_surface",
        () -> new CaveSurfaceDecorator(CaveSurfaceConfiguration.CODEC)
    );
    public static final Supplier<FeatureDecorator<NoneDecoratorConfiguration>> UNDERGROUND = DECORATOR.register(
        "underground",
        () -> new UndergroundDecorator(NoneDecoratorConfiguration.CODEC)
    );
}