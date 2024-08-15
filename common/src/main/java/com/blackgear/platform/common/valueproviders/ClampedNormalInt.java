package com.blackgear.platform.common.valueproviders;

import com.blackgear.platform.core.util.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

import java.util.Random;
import java.util.function.Function;

public class ClampedNormalInt extends IntProvider {
    public static final Codec<ClampedNormalInt> CODEC = RecordCodecBuilder.<ClampedNormalInt>create(instance -> {
        return instance.group(
            Codec.FLOAT.fieldOf("mean").forGetter(provider -> provider.mean),
            Codec.FLOAT.fieldOf("deviation").forGetter(provider -> provider.deviation),
            Codec.INT.fieldOf("min_inclusive").forGetter(provider -> provider.minInclusive),
            Codec.INT.fieldOf("max_inclusive").forGetter(provider -> provider.maxInclusive)
        ).apply(instance, ClampedNormalInt::new);
    }).comapFlatMap(provider -> {
        if (provider.maxInclusive < provider.minInclusive) {
            return DataResult.error("Max must be larger than min: [" + provider.minInclusive + ", " + provider.maxInclusive + "]");
        }

        return DataResult.success(provider);
    }, Function.identity());
    
    private final float mean;
    private final float deviation;
    private final int minInclusive;
    private final int maxInclusive;

    public static ClampedNormalInt of(float mean, float deviation, int minInclusive, int maxInclusive) {
        return new ClampedNormalInt(mean, deviation, minInclusive, maxInclusive);
    }

    public ClampedNormalInt(float mean, float deviation, int minInclusive, int maxInclusive) {
        this.mean = mean;
        this.deviation = deviation;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    @Override
    public int sample(Random random) {
        return sample(random, this.mean, this.deviation, (float)this.minInclusive, (float)this.maxInclusive);
    }

    public static int sample(Random random, float mean, float deviation, float minInclusive, float maxInclusive) {
        return (int) Mth.clamp(MathUtils.normal(random, mean, deviation), minInclusive, maxInclusive);
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
        return IntProviderType.CLAMPED_NORMAL;
    }

    @Override
    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}