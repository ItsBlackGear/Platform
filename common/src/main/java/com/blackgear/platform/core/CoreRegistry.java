package com.blackgear.platform.core;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public abstract class CoreRegistry<T> {
    protected final Registry<T> registry;
    protected final String modId;
    protected boolean isPresent = false;

    protected CoreRegistry(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    @ExpectPlatform
    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        throw new AssertionError();
    }

    @SuppressWarnings("UnusedReturnValue")
    public abstract <E extends T> Supplier<E> register(String key, Supplier<E> entry);

    public <E extends T> ResourceKey<T> registerKey(String key, Supplier<E> entry) {
        this.register(key, entry);
        return ResourceKey.create(this.registry.key(), new ResourceLocation(this.modId, key));
    }

    public void register() {
        if (this.isPresent) {
            throw new IllegalArgumentException("Duplication of Registry: " + this.registry);
        }
        this.isPresent = true;
        this.bootstrap();
    }

    protected abstract void bootstrap();
}