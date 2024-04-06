package com.blackgear.platform.common.entity.forge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;

public class EntityHandlerImpl {
    public static void addAttributes(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
        FMLJavaModLoadingContext.get().getModEventBus().<EntityAttributeCreationEvent>addListener(event -> event.put(type.get(), builder.get().build()));
    }
}
