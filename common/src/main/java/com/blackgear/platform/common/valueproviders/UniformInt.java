package com.blackgear.platform.common.valueproviders;

import com.blackgear.platform.core.util.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Random;
import java.util.function.Function;

public class UniformInt extends IntProvider {
    public static final Codec<UniformInt> CODEC = RecordCodecBuilder.<UniformInt>create(instance -> {
        return instance.group(
            Codec.INT.fieldOf("min_inclusive").forGetter(provider -> provider.minInclusive),
            Codec.INT.fieldOf("max_inclusive").forGetter(provider -> provider.maxInclusive)
        ).apply(instance, UniformInt::new);
    }).comapFlatMap(provider -> {
        if (provider.maxInclusive < provider.minInclusive) {
            return DataResult.error("Max must be at least min, min_inclusive: " + provider.minInclusive + ", max_inclusive: " + provider.maxInclusive);
        }

        return DataResult.success(provider);
    }, Function.identity());
    private final int minInclusive;
    private final int maxInclusive;

    public UniformInt(int minInclusive, int maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public static UniformInt of(int minInclusive, int maxInclusive) {
        return new UniformInt(minInclusive, maxInclusive);
    }

    @Override
    public int sample(Random random) {
        return MathUtils.randomBetweenInclusive(random, this.minInclusive, this.maxInclusive);
    }

    @Override
    public int getMinValue() {
        return this.minInclusive;
    }

    @Override
    public int getMaxValue() {
        return this.maxInclusive;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.UNIFORM;
    }

    @Override
    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}