package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class CoreRegistryImpl<T> extends CoreRegistry<T> {
    private final Registry<T> registry;

    protected CoreRegistryImpl(Registry<T> registry, String modId) {
        super(modId);
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public static <T> CoreRegistry<T> create(ResourceKey<? extends Registry<T>> key, String modId) {
        Registry<?> registry = BuiltInRegistries.REGISTRY.get(key.location());
        if (registry == null) throw new IllegalArgumentException("Unknown registry: " + key.location());

        return new CoreRegistryImpl<>((Registry<T>) registry, modId);
    }

    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        return new CoreRegistryImpl<>(registry, modId);
    }

    @Override
    public <E extends T> Supplier<E> register(String name, Supplier<E> entry) {
        E value = Registry.register(this.registry, new ResourceLocation(this.modId, name), entry.get());
        this.entries.add(() -> value);
        return () -> value;
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return this.registry.key();
    }

    @Override
    public Registry<T> registry() {
        return this.registry;
    }

    @Override
    protected void bootstrap() {
        // Nothing to do in Fabric, registration is immediate
    }
}