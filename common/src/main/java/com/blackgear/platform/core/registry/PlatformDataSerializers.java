package com.blackgear.platform.core.registry;

import com.blackgear.platform.common.entity.resource.EntityState;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class PlatformDataSerializers {
    public static final EntityDataSerializer<EntityState> ENTITY_STATE = EntityDataSerializer.simpleId(PlatformRegistries.ENTITY_STATES.registry());

    public static void bootstrap() {
        EntityDataSerializers.registerSerializer(ENTITY_STATE);
    }
}