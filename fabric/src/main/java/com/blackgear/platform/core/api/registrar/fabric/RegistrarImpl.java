package com.blackgear.platform.core.api.registrar.fabric;

import com.blackgear.platform.core.api.registrar.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RegistrarImpl<T> extends Registrar<T> {
    public static final Set<ResourceKey<?>> VANILLA_ENTRIES = ConcurrentHashMap.newKeySet();
    public RegistrarImpl(Registry<T> registry, String modId) {
        super(registry, modId);
    }

    public static <T> Registrar<T> create(Registry<T> registry, String modId) {
        return new RegistrarImpl<>(registry, modId);
    }

    @SuppressWarnings("unchecked")
    public static <T> Registrar<T> create(ResourceKey<? extends Registry<T>> key, String modId) {
        Registry<?> registry = BuiltInRegistries.REGISTRY.get(key.location());
        if (registry == null) throw new IllegalArgumentException("Unknown registry: " + key.location());
        return new RegistrarImpl<>((Registry<T>) registry, modId);
    }

    public static <T> void bind(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> consumer) {
        consumer.accept((identifier, entry) -> {
            if (identifier.getNamespace().equals("minecraft")) VANILLA_ENTRIES.add(ResourceKey.create(registry.key(), identifier));
            Registry.register(registry, identifier, entry);
        });
    }
}