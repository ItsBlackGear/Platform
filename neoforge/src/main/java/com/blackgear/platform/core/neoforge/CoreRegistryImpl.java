package com.blackgear.platform.core.neoforge;

import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.RegistryHolder;
import com.blackgear.platform.core.util.EventBus;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
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

    @Override
    public <E extends T> Supplier<E> register(String name, Supplier<E> entry) {
        return this.registry.register(name, entry);
    }

    @Override
    public <E extends T> Holder<T> holder(String name, Supplier<E> entry) {
        return this.registry.register(name, entry);
    }

    @Override @SuppressWarnings("unchecked")
    public <E extends T> RegistryHolder<E> registerHolder(String name, Supplier<E> entry) {
        DeferredHolder<T, E> registered = this.registry.register(name, entry);
        return new RegistryHolder<>() {
            @Override
            public E get() {
                return registered.get();
            }

            @Override
            public Optional<Holder<E>> getHolder() {
                return Optional.of((Holder<E>) registered);
            }

            @Override
            public boolean isPresent() {
                return registered.isBound();
            }

            @Override
            public ResourceLocation getId() {
                return registered.getId();
            }

            @Override
            public ResourceKey<E> getKey() {
                return (ResourceKey<E>) registered.getKey();
            }
        };
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
        this.registry.register(EventBus.get(EventBus.MOD));
    }
}