package com.blackgear.platform.common.worldgen.decorator;

import com.blackgear.platform.common.providers.math.ConstantInt;
import com.blackgear.platform.common.providers.math.IntProvider;
import com.blackgear.platform.common.registry.PlatformDecorators;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;

public class CountConfiguration implements DecoratorConfiguration {
    public static final Codec<CountConfiguration> CODEC = IntProvider.createCodec(0, 256)
        .fieldOf("count")
        .xmap(CountConfiguration::new, config -> config.count)
        .codec();
    
    public final IntProvider count;
    
    public CountConfiguration(IntProvider count) {
        this.count = count;
    }
    
    public static ConfiguredDecorator<?> of(IntProvider count) {
        return PlatformDecorators.COUNT.get().configured(new CountConfiguration(count));
    }

    public static ConfiguredDecorator<?> of(int count) {
        return of(ConstantInt.of(count));
    }
}