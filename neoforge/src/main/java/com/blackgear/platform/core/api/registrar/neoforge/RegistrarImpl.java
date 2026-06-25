package com.blackgear.platform.core.api.registrar.neoforge;

import com.blackgear.platform.core.api.registrar.Registrar;
import com.blackgear.platform.core.util.EventBus;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RegistrarImpl<T> extends Registrar<T> {
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

    public static <T> void bind(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> bootstrap) {
        EventBus.get(EventBus.MOD).addListener((RegisterEvent event) -> {
            if (registry.key().equals(event.getRegistryKey())) {
                bootstrap.accept((identifier, entry) -> event.register(registry.key(), identifier, () -> entry));
            }
        });
    }
}