package com.blackgear.platform.common.providers.height;

import com.blackgear.platform.common.worldgen.height.VerticalAnchor;
import com.blackgear.platform.common.worldgen.WorldGenerationContext;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Random;

public class ConstantHeight extends HeightProvider {
    public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.absolute(0));
    public static final Codec<ConstantHeight> CODEC = Codec.either(VerticalAnchor.CODEC, RecordCodecBuilder.<ConstantHeight>create(instance -> {
        return instance.group(VerticalAnchor.CODEC.fieldOf("value").forGetter(provider -> provider.value)).apply(instance, ConstantHeight::new);
    })).xmap(either -> {
        return either.map(ConstantHeight::of, provider -> provider);
    }, provider -> {
        return Either.left(provider.value);
    });
    private final VerticalAnchor value;
    
    public static ConstantHeight of(VerticalAnchor verticalAnchor) {
        return new ConstantHeight(verticalAnchor);
    }
    
    private ConstantHeight(VerticalAnchor verticalAnchor) {
        this.value = verticalAnchor;
    }
    
    public VerticalAnchor getValue() {
        return this.value;
    }
    
    @Override
    public int sample(Random random, WorldGenerationContext context) {
        return this.value.resolveY(context);
    }
    
    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.CONSTANT;
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
}