package com.blackgear.platform.common.integration.fabric;

import com.blackgear.platform.common.integration.MobIntegration;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import java.util.function.Consumer;

public class MobIntegrationImpl {
    public static void registerIntegrations(Consumer<MobIntegration.Event> listener) {
        listener.accept((type, builder) -> FabricDefaultAttributeRegistry.register(type.get(), builder.get()));
    }
}
