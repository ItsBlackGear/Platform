package com.blackgear.platform.common.providers.math;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.registry.PlatformRegistries;
import com.mojang.serialization.Codec;

public interface FloatProviderType<P extends FloatProvider> {
    CoreRegistry<FloatProviderType<?>> PROVIDERS = CoreRegistry.create(PlatformRegistries.FLOAT_PROVIDER_TYPE.getRegistry(), Platform.MOD_ID);

    FloatProviderType<ConstantFloat> CONSTANT = PROVIDERS.registerVanilla("constant", () -> ConstantFloat.CODEC);
    FloatProviderType<UniformFloat> UNIFORM = PROVIDERS.registerVanilla("uniform", () -> UniformFloat.CODEC);
    FloatProviderType<ClampedNormalFloat> CLAMPED_NORMAL = PROVIDERS.registerVanilla("clamped_normal", () -> ClampedNormalFloat.CODEC);

    Codec<P> codec();
}