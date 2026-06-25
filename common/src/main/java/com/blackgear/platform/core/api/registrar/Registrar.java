package com.blackgear.platform.core.api.registrar;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class Registrar<T> {
    protected final LinkedHashMap<ResourceLocation, T> entries = new LinkedHashMap<>();
    public final Registry<T> registry;
    protected final String modId;
    protected boolean isPresent = false;

    public Registrar(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    @ExpectPlatform
    public static <T> Registrar<T> create(ResourceKey<? extends Registry<T>> key, String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> Registrar<T> create(Registry<T> registry, String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> void bind(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> consumer) {
        throw new AssertionError();
    }

    public <E extends T> E register(String name, E entry) {
        this.entries.put(ResourceLocation.fromNamespaceAndPath(this.modId, name), entry);
        return entry;
    }

    public <E extends T> ResourceKey<T> resource(String name, E entry) {
        this.register(name, entry);
        return ResourceKey.create(this.registry.key(), ResourceLocation.fromNamespaceAndPath(this.modId, name));
    }

    public void registrar() {
        if (this.isPresent) return;

        this.isPresent = true;
        this.register();
    }

    protected void register() {
        bind(this.registry, this.entries::forEach);
    }
}