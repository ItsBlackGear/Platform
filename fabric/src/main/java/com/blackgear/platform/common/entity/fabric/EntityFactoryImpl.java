package com.blackgear.platform.common.entity.fabric;

import com.blackgear.platform.common.entity.EntityFactory;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityFactoryImpl {
    public static void registerMobAttributes(Consumer<EntityFactory.EntityAttributesEvent> listener) {
        listener.accept((type, builder) -> FabricDefaultAttributeRegistry.register(type.get(), builder.get()));
    }
}