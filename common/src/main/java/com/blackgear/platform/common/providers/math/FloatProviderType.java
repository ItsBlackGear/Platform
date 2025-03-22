package com.blackgear.platform.common.providers.math;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.registry.PlatformRegistries;
import com.mojang.serialization.Codec;

public interface FloatProviderType<P extends FloatProvider> {
    CoreRegistry<FloatProviderType<?>> PROVIDERS = CoreRegistry.create(PlatformRegistries.FLOAT_PROVIDER_TYPE.getRegistry(), Platform.MOD_ID);

    FloatProviderType<ConstantFloat> CONSTANT = PROVIDERS.vanilla("constant", () -> ConstantFloat.CODEC);
    FloatProviderType<UniformFloat> UNIFORM = PROVIDERS.vanilla("uniform", () -> UniformFloat.CODEC);
    FloatProviderType<ClampedNormalFloat> CLAMPED_NORMAL = PROVIDERS.vanilla("clamped_normal", () -> ClampedNormalFloat.CODEC);

    Codec<P> codec();
}