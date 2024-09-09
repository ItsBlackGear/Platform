package com.blackgear.platform.common.worldgen.decorator;

import com.blackgear.platform.common.providers.math.ConstantInt;
import com.blackgear.platform.common.providers.math.IntProvider;
import com.blackgear.platform.common.registry.PlatformDecorators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;

public class RandomOffsetConfiguration implements DecoratorConfiguration {
    public static final Codec<RandomOffsetConfiguration> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            IntProvider.createCodec(-16, 16).fieldOf("xz_spread").forGetter(config -> config.xzSpread),
            IntProvider.createCodec(-16, 16).fieldOf("y_spread").forGetter(config -> config.ySpread)
        ).apply(instance, RandomOffsetConfiguration::new);
    });
    
    public final IntProvider xzSpread;
    public final IntProvider ySpread;
    
    public RandomOffsetConfiguration(IntProvider xzSpread, IntProvider ySpread) {
        this.xzSpread = xzSpread;
        this.ySpread = ySpread;
    }
    
    public static ConfiguredDecorator<?> of(IntProvider xzSpread, IntProvider ySpread) {
        return PlatformDecorators.RANDOM_OFFSET.get().configured(new RandomOffsetConfiguration(xzSpread, ySpread));
    }
    
    public static ConfiguredDecorator<?> vertical(IntProvider ySpread) {
        return of(ConstantInt.ZERO, ySpread);
    }
    
    public static ConfiguredDecorator<?> horizontal(IntProvider xzSpread) {
        return of(xzSpread, ConstantInt.ZERO);
    }
}