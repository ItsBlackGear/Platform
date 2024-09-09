package com.blackgear.platform.common.providers.height;

import com.blackgear.platform.common.worldgen.height.VerticalAnchor;
import com.blackgear.platform.common.worldgen.WorldGenerationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class BiasedToBottomHeight extends HeightProvider {
    public static final Codec<BiasedToBottomHeight> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter(provider -> provider.minInclusive),
            VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter(provider -> provider.maxInclusive),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("inner", 1).forGetter(provider -> provider.inner)
        )
        .apply(instance, BiasedToBottomHeight::new);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    private final int inner;
    
    private BiasedToBottomHeight(VerticalAnchor minInclusive, VerticalAnchor maxInclusive, int inner) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        this.inner = inner;
    }
    
    public static BiasedToBottomHeight of(VerticalAnchor minInclusive, VerticalAnchor maxInclusive, int inner) {
        return new BiasedToBottomHeight(minInclusive, maxInclusive, inner);
    }
    
    @Override
    public int sample(Random random, WorldGenerationContext context) {
        int minY = this.minInclusive.resolveY(context);
        int maxY = this.maxInclusive.resolveY(context);
        
        if (maxY - minY - this.inner + 1 <= 0) {
            LOGGER.warn("Empty height range: {}", this);
            return minY;
        } else {
            int bound = random.nextInt(maxY - minY - this.inner + 1);
            return random.nextInt(bound + this.inner) + minY;
        }
    }
    
    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.BIASED_TO_BOTTOM;
    }
    
    @Override
    public String toString() {
        return "biased[" + this.minInclusive + "-" + this.maxInclusive + " inner: " + this.inner + "]";
    }
}