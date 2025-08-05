package com.blackgear.platform.core.helper.fabric;

import com.blackgear.platform.core.helper.DynamicRegistry;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

import java.util.function.Consumer;

public class DynamicRegistryImpl {
    public static void onRegister(Consumer<DynamicRegistry.Registrar> listener) {
        listener.accept(DynamicRegistries::registerSynced);
    }
}