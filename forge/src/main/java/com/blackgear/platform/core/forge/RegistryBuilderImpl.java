package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.RegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class RegistryBuilderImpl {
    public static RegistryBuilder create(String modId) {
        return new RegistryBuilder(modId) {
            @Override
            public <T> Supplier<Registry<T>> registry(ResourceKey<Registry<T>> key) {
                if (BuiltInRegistries.REGISTRY instanceof MappedRegistry<? extends Registry<?>> registry) {
                    registry.unfreeze();
                }

                Registry<T> registry = BuiltInRegistries.registerSimple(key, builder -> (T) new Object());

                if (BuiltInRegistries.REGISTRY instanceof MappedRegistry<? extends Registry<?>> mappedRegistry) {
                    mappedRegistry.freeze();
                }

                return () -> registry;
            }
        };
    }
}