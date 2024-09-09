package com.blackgear.platform.common.providers.math;

import com.blackgear.platform.core.registry.PlatformRegistries;
import com.blackgear.platform.core.util.ExtraCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Random;

public abstract class IntProvider {
    private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(
        Codec.INT,
        PlatformRegistries.INT_PROVIDER_TYPE.getRegistry().dispatch(IntProvider::getType, IntProviderType::codec)
    );
    public static final Codec<IntProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap(either -> {
        return either.map(ConstantInt::of, provider -> {
            return provider;
        });
    }, provider -> {
        return provider.getType() == IntProviderType.CONSTANT
            ? Either.left(((ConstantInt) provider).getValue())
            : Either.right(provider);
    });
    
    public static final Codec<IntProvider> NON_NEGATIVE_CODEC = createCodec(0, Integer.MAX_VALUE);
    public static final Codec<IntProvider> POSITIVE_CODEC = createCodec(1, Integer.MAX_VALUE);
    
    /**
     * Create a codec for IntProvider with the specified minimum and maximum values.
     *
     * @param minValue The minimum value for the IntProvider.
     * @param maxValue The maximum value for the IntProvider.
     * @return The created Codec.
     */
    public static Codec<IntProvider> createCodec(int minValue, int maxValue) {
        return createCodec(minValue, maxValue, CODEC);
    }
    
    /**
     * Create a codec for a specific IntProvider type with the specified minimum and maximum values.
     *
     * @param minValue The minimum value for the IntProvider.
     * @param maxValue The maximum value for the IntProvider.
     * @param codec    The codec for the IntProvider type.
     * @param <T>      The type of the IntProvider.
     * @return The created Codec.
     */
    private static <T extends IntProvider> Codec<T> createCodec(int minValue, int maxValue, Codec<T> codec) {
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
     * Sample an integer value from the IntProvider using the specified random source.
     *
     * @return The sampled integer value.
     */
    public abstract int sample(Random random);
    
    /**
     * Get the minimum value of the IntProvider.
     *
     * @return The minimum value.
     */
    public abstract int getMinValue();
    
    /**
     * Get the maximum value of the IntProvider.
     *
     * @return The maximum value.
     */
    public abstract int getMaxValue();
    
    /**
     * Get the type of the IntProvider.
     *
     * @return The IntProviderType.
     */
    public abstract IntProviderType<?> getType();
}