package com.blackgear.platform.common.valueproviders;

import com.blackgear.platform.core.util.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Random;
import java.util.function.Function;

public class UniformFloat extends FloatProvider {
    public static final Codec<UniformFloat> CODEC = RecordCodecBuilder.<UniformFloat>create(instance -> {
        return instance.group(
            Codec.FLOAT.fieldOf("min_inclusive").forGetter(provider -> provider.minInclusive),
            Codec.FLOAT.fieldOf("max_exclusive").forGetter(provider -> provider.maxExclusive)
        ).apply(instance, UniformFloat::new);
    }).comapFlatMap(provider -> {
        if (provider.maxExclusive <= provider.minInclusive) {
            return DataResult.error("Max must be at least min, min_inclusive: " + provider.minInclusive + ", max_inclusive: " + provider.maxExclusive);
        }

        return DataResult.success(provider);
    }, Function.identity());
    private final float minInclusive;
    private final float maxExclusive;

    public UniformFloat(float minInclusive, float maxExclusive) {
        this.minInclusive = minInclusive;
        this.maxExclusive = maxExclusive;
    }

    public static UniformFloat of(float minInclusive, float maxExclusive) {
        if (maxExclusive <= minInclusive) {
            throw new IllegalArgumentException("Max must exceed min");
        } else {
            return new UniformFloat(minInclusive, maxExclusive);
        }
    }

    @Override
    public float sample(Random random) {
        return MathUtils.randomBetween(random, this.minInclusive, this.maxExclusive);
    }

    @Override
    public float getMinValue() {
        return this.minInclusive;
    }

    @Override
    public float getMaxValue() {
        return this.maxExclusive;
    }

    @Override
    public FloatProviderType<?> getType() {
        return FloatProviderType.UNIFORM;
    }

    @Override
    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxExclusive + "]";
    }
}