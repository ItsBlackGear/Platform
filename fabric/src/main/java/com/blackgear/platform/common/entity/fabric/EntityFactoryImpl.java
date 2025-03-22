package com.blackgear.platform.common.entity.fabric;

import com.blackgear.platform.common.entity.EntityFactory;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import java.util.function.Consumer;

public class EntityFactoryImpl {
    public static void registerMobAttributes(Consumer<EntityFactory.EntityAttributesEvent> listener) {
        listener.accept((type, builder) -> FabricDefaultAttributeRegistry.register(type.get(), builder.get()));
    }
}