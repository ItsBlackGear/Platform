package com.blackgear.platform.core.helper.forge;

import com.blackgear.platform.core.helper.DynamicRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.function.Consumer;

public class DynamicRegistryImpl {
    public static void onRegister(Consumer<DynamicRegistry.Registrar> listener) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DynamicRegistry.Registrar registrar = new DynamicRegistry.Registrar() {
            @Override
            public <T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
                bus.addListener((DataPackRegistryEvent.NewRegistry event) -> event.dataPackRegistry(key, codec));
            }
        };
        listener.accept(registrar);
    }
}
