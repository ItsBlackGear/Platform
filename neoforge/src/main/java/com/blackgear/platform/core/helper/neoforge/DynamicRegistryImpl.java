package com.blackgear.platform.core.helper.neoforge;

import com.blackgear.platform.core.helper.DynamicRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.function.Consumer;

public class DynamicRegistryImpl {
    public static void onRegister(Consumer<DynamicRegistry.Registrar> listener) {
        IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
        DynamicRegistry.Registrar registrar = new DynamicRegistry.Registrar() {
            @Override
            public <T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
                bus.addListener((DataPackRegistryEvent.NewRegistry event) -> event.dataPackRegistry(key, codec));
            }
        };
        listener.accept(registrar);
    }
}