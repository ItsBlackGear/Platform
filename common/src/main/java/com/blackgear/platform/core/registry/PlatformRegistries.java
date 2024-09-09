package com.blackgear.platform.core.registry;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.providers.height.HeightProviderType;
import com.blackgear.platform.common.providers.math.FloatProviderType;
import com.blackgear.platform.common.providers.math.IntProviderType;
import com.blackgear.platform.core.RegistryBuilder;

public class PlatformRegistries {
    public static final RegistryBuilder BUILDER = new RegistryBuilder(Platform.MOD_ID);
    
    public static final RegistryBuilder.Sample<IntProviderType<?>> INT_PROVIDER_TYPE = BUILDER.create("int_provider_type", () -> IntProviderType.CONSTANT);
    public static final RegistryBuilder.Sample<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = BUILDER.create("float_provider_type", () -> FloatProviderType.CONSTANT);
    public static final RegistryBuilder.Sample<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = BUILDER.create("height_provider_type", () -> HeightProviderType.CONSTANT);
}