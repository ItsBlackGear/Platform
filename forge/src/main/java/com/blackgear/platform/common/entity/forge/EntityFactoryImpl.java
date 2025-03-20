package com.blackgear.platform.common.entity.forge;

import com.blackgear.platform.common.entity.EntityFactory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;

public class EntityFactoryImpl {
    public static void registerMobAttributes(Consumer<EntityFactory.EntityAttributesEvent> listener) {
        Consumer<EntityAttributeCreationEvent> consumer = event -> listener.accept((type, builder) -> event.put(type.get(), builder.get().build()));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }
}