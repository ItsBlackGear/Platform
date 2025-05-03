package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.RegistryHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
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
        E value = Registry.register(this.registry, ResourceLocation.fromNamespaceAndPath(this.modId, name), entry.get());
        this.entries.add(() -> value);
        return () -> value;
    }

    @Override @SuppressWarnings("unchecked")
    public <E extends T> RegistryHolder<E> registerHolder(String name, Supplier<E> entry) {
        ResourceLocation value = ResourceLocation.fromNamespaceAndPath(this.modId, name);
        E registered = Registry.register(this.registry, value, entry.get());
        this.entries.add(() -> registered);
        return new RegistryHolder<>() {
            final ResourceKey<E> key = ResourceKey.create((ResourceKey<? extends Registry<E>>) registry.key(), value);

            @Override
            public E get() {
                return registered;
            }

            @Override
            public Optional<Holder<E>> getHolder() {
                Holder<E> holder = (Holder<E>) registry.getHolder((ResourceKey<T>) key).orElse(null);
                return Optional.ofNullable(holder);
            }

            @Override
            public boolean isPresent() {
                return registry.containsKey(value);
            }

            @Override
            public ResourceLocation getId() {
                return value;
            }

            @Override
            public ResourceKey<E> getKey() {
                return key;
            }
        };
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