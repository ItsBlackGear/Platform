package com.blackgear.platform.common.providers.height;

import com.blackgear.platform.common.worldgen.height.VerticalAnchor;
import com.blackgear.platform.common.worldgen.WorldGenerationContext;
import com.blackgear.platform.core.util.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class UniformHeight extends HeightProvider {
    public static final Codec<UniformHeight> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter(provider -> provider.minInclusive),
            VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter(provider -> provider.maxInclusive)
        )
        .apply(instance, UniformHeight::new);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    
    private UniformHeight(VerticalAnchor minInclusive, VerticalAnchor maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }
    
    public static UniformHeight of(VerticalAnchor minInclusive, VerticalAnchor maxInclusive) {
        return new UniformHeight(minInclusive, maxInclusive);
    }
    
    @Override
    public int sample(Random random, WorldGenerationContext context) {
        int minY = this.minInclusive.resolveY(context);
        int maxY = this.maxInclusive.resolveY(context);
        if (minY > maxY) {
            LOGGER.warn("Empty height range: {}", this);
            return minY;
        } else {
            return MathUtils.randomBetweenInclusive(random, minY, maxY);
        }
    }
    
    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.UNIFORM;
    }
    
    @Override
    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}