package com.blackgear.platform.core.helper.forge;

import com.blackgear.platform.core.helper.DynamicRegistry;
import com.blackgear.platform.core.util.EventBus;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.function.Consumer;

public class DynamicRegistryImpl {
    public static void onRegister(Consumer<DynamicRegistry.Registrar> listener) {
        listener.accept(new DynamicRegistry.Registrar() {
            @Override
            public <T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
                EventBus.get(EventBus.MOD).addListener((DataPackRegistryEvent.NewRegistry event) -> event.dataPackRegistry(key, codec));
            }
        });
    }
}
