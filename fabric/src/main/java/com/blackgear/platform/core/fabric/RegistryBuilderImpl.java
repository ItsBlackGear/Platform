package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.RegistryBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class RegistryBuilderImpl {
    public static RegistryBuilder create(String modId) {
        return new RegistryBuilder(modId) {
            @Override
            public <T> Supplier<Registry<T>> registry(ResourceKey<Registry<T>> key) {
                MappedRegistry<T> registry = FabricRegistryBuilder.createSimple(key).buildAndRegister();
                return () -> registry;
            }
        };
    }
}