package com.blackgear.platform.common.worldgen.decorator;

import com.blackgear.platform.common.providers.height.HeightProvider;
import com.blackgear.platform.common.providers.height.TrapezoidHeight;
import com.blackgear.platform.common.providers.height.UniformHeight;
import com.blackgear.platform.common.registry.PlatformDecorators;
import com.blackgear.platform.common.worldgen.height.VerticalAnchor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;

public class RangedConfiguration implements DecoratorConfiguration {
    public static final Codec<RangedConfiguration> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(HeightProvider.CODEC.fieldOf("height").forGetter(config -> config.height))
            .apply(instance, RangedConfiguration::new);
    });
    public final HeightProvider height;
    
    public RangedConfiguration(HeightProvider height) {
        this.height = height;
    }
    
    public static ConfiguredDecorator<?> of(HeightProvider height) {
        return PlatformDecorators.RANGE.get().configured(new RangedConfiguration(height));
    }
    
    public static ConfiguredDecorator<?> uniform(VerticalAnchor minInclusive, VerticalAnchor maxInclusive) {
        return of(UniformHeight.of(minInclusive, maxInclusive));
    }
    
    public static ConfiguredDecorator<?> triangle(VerticalAnchor minInclusive, VerticalAnchor maxInclusive) {
        return of(TrapezoidHeight.of(minInclusive, maxInclusive));
    }
}