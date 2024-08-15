package com.blackgear.platform.common.valueproviders;

import com.blackgear.platform.core.util.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

import java.util.Random;
import java.util.function.Function;

public class ClampedNormalFloat extends FloatProvider {
    public static final Codec<ClampedNormalFloat> CODEC = RecordCodecBuilder.<ClampedNormalFloat>create(instance -> {
        return instance.group(
            Codec.FLOAT.fieldOf("mean").forGetter(provider -> provider.mean),
            Codec.FLOAT.fieldOf("deviation").forGetter(provider -> provider.deviation),
            Codec.FLOAT.fieldOf("min").forGetter(provider -> provider.min),
            Codec.FLOAT.fieldOf("max").forGetter(provider -> provider.max)
        ).apply(instance, ClampedNormalFloat::new);
    }).comapFlatMap(provider -> {
        if (provider.max < provider.min) {
            return DataResult.error("Max must be larger than min: [" + provider.min + ", " + provider.max + "]");
        }

        return DataResult.success(provider);
    }, Function.identity());
    
    private final float mean;
    private final float deviation;
    private final float min;
    private final float max;

    public static ClampedNormalFloat of(float mean, float deviation, float min, float max) {
        return new ClampedNormalFloat(mean, deviation, min, max);
    }

    private ClampedNormalFloat(float mean, float deviation, float min, float max) {
        this.mean = mean;
        this.deviation = deviation;
        this.min = min;
        this.max = max;
    }

    @Override
    public float sample(Random random) {
        return sample(random, this.mean, this.deviation, this.min, this.max);
    }

    public static float sample(Random random, float mean, float deviation, float min, float max) {
        return Mth.clamp(MathUtils.normal(random, mean, deviation), min, max);
    }

    @Override
    public float getMinValue() {
        return this.min;
    }

    @Override
    public float getMaxValue() {
        return this.max;
    }

    @Override
    public FloatProviderType<?> getType() {
        return FloatProviderType.CLAMPED_NORMAL;
    }

    @Override
    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
    }
}