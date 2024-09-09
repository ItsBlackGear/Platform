package com.blackgear.platform.common.providers.height;

import com.blackgear.platform.common.worldgen.height.VerticalAnchor;
import com.blackgear.platform.common.worldgen.WorldGenerationContext;
import com.blackgear.platform.core.util.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class TrapezoidHeight extends HeightProvider {
    public static final Codec<TrapezoidHeight> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter(provider -> provider.minInclusive),
            VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter(provider -> provider.maxInclusive),
            Codec.INT.optionalFieldOf("plateau", 0).forGetter(provider -> provider.plateau)
        )
        .apply(instance, TrapezoidHeight::new);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    private final int plateau;
    
    private TrapezoidHeight(VerticalAnchor minInclusive, VerticalAnchor maxInclusive, int plateau) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        this.plateau = plateau;
    }
    
    public static TrapezoidHeight of(VerticalAnchor minInclusive, VerticalAnchor maxInclusive, int plateau) {
        return new TrapezoidHeight(minInclusive, maxInclusive, plateau);
    }
    
    public static TrapezoidHeight of(VerticalAnchor minInclusive, VerticalAnchor maxInclusive) {
        return of(minInclusive, maxInclusive, 0);
    }
    
    @Override
    public int sample(Random random, WorldGenerationContext context) {
        int minY = this.minInclusive.resolveY(context);
        int maxY = this.maxInclusive.resolveY(context);
        if (minY > maxY) {
            LOGGER.warn("Empty height range: {}", this);
            return minY;
        } else {
            int heightRange = maxY - minY;
            if (this.plateau >= heightRange) {
                return MathUtils.randomBetweenInclusive(random, minY, maxY);
            } else {
                int lowerRange = (heightRange - this.plateau) / 2;
                int upperRange = heightRange - lowerRange;
                return minY + MathUtils.randomBetweenInclusive(random, 0, upperRange) + MathUtils.randomBetweenInclusive(random, 0, lowerRange);
            }
        }
    }
    
    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.TRAPEZOID;
    }
    
    @Override
    public String toString() {
        return this.plateau == 0
            ? "triangle (" + this.minInclusive + "-" + this.maxInclusive + ")"
            : "trapezoid(" + this.plateau + ") in [" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}