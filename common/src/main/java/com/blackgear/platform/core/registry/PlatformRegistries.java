package com.blackgear.platform.core.registry;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.entity.resource.EntityState;
import com.blackgear.platform.core.RegistryBuilder;

public class PlatformRegistries {
    public static final RegistryBuilder BUILDER = new RegistryBuilder(Platform.MOD_ID);

    public static final RegistryBuilder.Sample<EntityState> ENTITY_STATES = BUILDER.create("entity_state", registry -> EntityState.IDLE.get());
}