package com.blackgear.platform.core.neoforge;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CoreRegistryImpl<T> extends CoreRegistry<T> {
    private final DeferredRegister<T> registry;

    protected CoreRegistryImpl(DeferredRegister<T> registry, String modId) {
        super(modId);
        this.registry = registry;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CoreRegistry<T> create(ResourceKey<? extends Registry<T>> key, String modId) {
        return new CoreRegistryImpl(DeferredRegister.create(key, modId), modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        return new CoreRegistryImpl(DeferredRegister.create(registry.key(), modId), modId);
    }

    @Override @SuppressWarnings("unchecked")
    public <E extends T> Supplier<E> register(String name, Supplier<E> entry) {
        DeferredHolder<T, E> value = this.registry.register(name, entry);
        this.entries.add((Supplier<T>) value);
        return value;
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return this.registry.getRegistryKey();
    }

    @Override
    public Registry<T> registry() {
        return get(key());
    }

    @SuppressWarnings("unchecked")
    private static <T> T get(ResourceKey<?> key) {
        return ((Registry<T>) BuiltInRegistries.REGISTRY).get((ResourceKey<T>) key);
    }

    @Override
    protected void bootstrap() {
        this.registry.register(ModLoadingContext.get().getActiveContainer().getEventBus());
    }
}