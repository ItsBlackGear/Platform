package com.blackgear.platform.common.integration.forge;

import com.blackgear.platform.common.integration.MobIntegration;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;

public class MobIntegrationImpl {
    public static void registerIntegrations(Consumer<MobIntegration.Event> listener) {
        Consumer<EntityAttributeCreationEvent> consumer = event -> listener.accept((type, builder) -> event.put(type.get(), builder.get().build()));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }
}