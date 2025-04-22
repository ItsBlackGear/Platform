package com.blackgear.platform.common.integration.neoforge;

import com.blackgear.platform.common.integration.MobIntegration;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import java.util.function.Consumer;

public class MobIntegrationImpl {
    public static void registerIntegrations(Consumer<MobIntegration.Event> listener) {
        Consumer<EntityAttributeCreationEvent> consumer = event -> listener.accept((type, builder) -> event.put(type.get(), builder.get().build()));
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener(consumer);
    }
}