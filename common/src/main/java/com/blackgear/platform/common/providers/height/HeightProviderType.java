package com.blackgear.platform.common.providers.height;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.registry.PlatformRegistries;
import com.mojang.serialization.Codec;

public interface HeightProviderType<P extends HeightProvider> {
    CoreRegistry<HeightProviderType<?>> PROVIDERS = CoreRegistry.create(PlatformRegistries.HEIGHT_PROVIDER_TYPE.getRegistry(), Platform.MOD_ID);
    
    HeightProviderType<ConstantHeight> CONSTANT = PROVIDERS.registerVanilla("constant", () -> ConstantHeight.CODEC);
    HeightProviderType<UniformHeight> UNIFORM = PROVIDERS.registerVanilla("uniform", () -> UniformHeight.CODEC);
    HeightProviderType<BiasedToBottomHeight> BIASED_TO_BOTTOM = PROVIDERS.registerVanilla("biased_to_bottom", () -> BiasedToBottomHeight.CODEC);
    HeightProviderType<VeryBiasedToBottomHeight> VERY_BIASED_TO_BOTTOM = PROVIDERS.registerVanilla("very_biased_to_bottom", () -> VeryBiasedToBottomHeight.CODEC);
    HeightProviderType<TrapezoidHeight> TRAPEZOID = PROVIDERS.registerVanilla("trapezoid", () -> TrapezoidHeight.CODEC);
    
    Codec<P> codec();
}