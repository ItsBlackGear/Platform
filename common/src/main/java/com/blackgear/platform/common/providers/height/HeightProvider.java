package com.blackgear.platform.common.providers.height;

import com.blackgear.platform.common.worldgen.height.VerticalAnchor;
import com.blackgear.platform.common.worldgen.WorldGenerationContext;
import com.blackgear.platform.core.registry.PlatformRegistries;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.util.Random;

public abstract class HeightProvider {
    private static final Codec<Either<VerticalAnchor, HeightProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(
        VerticalAnchor.CODEC,
        PlatformRegistries.HEIGHT_PROVIDER_TYPE.getRegistry().dispatch(HeightProvider::getType, HeightProviderType::codec)
    );
    
    public static final Codec<HeightProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap(
        either -> either.map(ConstantHeight::of, provider -> provider),
        provider -> provider.getType() == HeightProviderType.CONSTANT
            ? Either.left(((ConstantHeight) provider).getValue())
            : Either.right(provider)
    );
    
    public abstract int sample(Random random, WorldGenerationContext context);
    
    public abstract HeightProviderType<?> getType();
}