package com.blackgear.platform.common.valueproviders;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.registry.PlatformRegistries;
import com.mojang.serialization.Codec;

public interface IntProviderType<P extends IntProvider> {
    CoreRegistry<IntProviderType<?>> PROVIDERS = CoreRegistry.create(PlatformRegistries.INT_PROVIDER_TYPE.getRegistry(), Platform.MOD_ID);
    
    IntProviderType<ConstantInt> CONSTANT = PROVIDERS.registerSimple("constant", () -> ConstantInt.CODEC);
    IntProviderType<UniformInt> UNIFORM = PROVIDERS.registerSimple("uniform", () -> UniformInt.CODEC);
    IntProviderType<ClampedNormalInt> CLAMPED_NORMAL = PROVIDERS.registerSimple("clamped_normal", () -> ClampedNormalInt.CODEC);
    
    Codec<P> codec();
}