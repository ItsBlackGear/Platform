package com.blackgear.platform.common.providers.math;

import com.blackgear.platform.core.registry.PlatformRegistries;
import com.blackgear.platform.core.util.ExtraCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Random;

public abstract class FloatProvider {
    private static final Codec<Either<Float, FloatProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(
        Codec.FLOAT,
        PlatformRegistries.FLOAT_PROVIDER_TYPE.getRegistry().dispatch(FloatProvider::getType, FloatProviderType::codec)
    );
    public static final Codec<FloatProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap(either -> {
        return either.map(ConstantFloat::of, provider -> {
            return provider;
        });
    }, provider -> {
        return provider.getType() == FloatProviderType.CONSTANT
            ? Either.left(((ConstantFloat) provider).getValue())
            : Either.right(provider);
    });

    /**
     * Create a codec for FloatProvider with the specified minimum and maximum values.
     *
     * @param minValue The minimum value for the FloatProvider.
     * @param maxValue The maximum value for the FloatProvider.
     * @return The created Codec.
     */
    public static Codec<FloatProvider> createCodec(float minValue, float maxValue) {
        return createCodec(minValue, maxValue, CODEC);
    }

    /**
     * Create a codec for a specific FloatProvider type with the specified minimum and maximum values.
     *
     * @param minValue The minimum value for the FloatProvider.
     * @param maxValue The maximum value for the FloatProvider.
     * @param codec    The codec for the FloatProvider type.
     * @param <T>      The type of the FloatProvider.
     * @return The created Codec.
     */
    private static <T extends FloatProvider> Codec<T> createCodec(float minValue, float maxValue, Codec<T> codec) {
        return ExtraCodecs.validate(codec, provider -> {
            if (provider.getMinValue() < minValue) {
                return DataResult.error("Value provider too low: " + minValue + " [" + provider.getMinValue() + "-" + provider.getMaxValue() + "]");
            } else {
                return provider.getMaxValue() > maxValue
                    ? DataResult.error("Value provider too high: " + maxValue + " [" + provider.getMinValue() + "-" + provider.getMaxValue() + "]")
                    : DataResult.success(provider);
            }
        });
    }

    /**
     * Sample a float value from the FloatProvider using the specified random source.
     *
     * @return The sampled float value.
     */
    public abstract float sample(Random random);

    /**
     * Get the minimum value of the FloatProvider.
     *
     * @return The minimum value.
     */
    public abstract float getMinValue();

    /**
     * Get the maximum value of the FloatProvider.
     *
     * @return The maximum value.
     */
    public abstract float getMaxValue();

    /**
     * Get the type of the FloatProvider.
     *
     * @return The FloatProviderType.
     */
    public abstract FloatProviderType<?> getType();
}