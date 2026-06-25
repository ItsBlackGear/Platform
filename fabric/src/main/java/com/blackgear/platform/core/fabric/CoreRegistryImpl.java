package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.RegistryHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CoreRegistryImpl<T> extends CoreRegistry<T> {
    public static final Set<ResourceKey<?>> VANILLA_KEYS = ConcurrentHashMap.newKeySet();
    private final Registry<T> registry;

    protected CoreRegistryImpl(Registry<T> registry, String modId) {
        super(modId);
        this.registry = registry;
    }

    public static <T> void register(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
        source.accept((entry, key) -> {
            ResourceKey<T> resourceKey = ResourceKey.create(registry, key);
            if (key.getNamespace().equals("minecraft")) VANILLA_KEYS.add(resourceKey);

            Registry<?> reg = BuiltInRegistries.REGISTRY.get(registry.location());
            if (reg == null) throw new IllegalArgumentException("Unknown registry: " + registry.location());
            Registry.register((Registry<T>) reg, key, entry);
        });
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
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(this.modId, name);
        ResourceKey<T> resourceKey = ResourceKey.create(this.registry.key(), location);

        if (this.modId.equals("minecraft")) VANILLA_KEYS.add(resourceKey);

        E value = Registry.register(this.registry, location, entry.get());
        return () -> value;
    }

    @Override
    public <E extends T> Holder<T> holder(String name, Supplier<E> entry) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(this.modId, name);

        if (this.modId.equals("minecraft")) VANILLA_KEYS.add(ResourceKey.create(this.registry.key(), location));

        return Registry.registerForHolder(this.registry, location, entry.get());
    }

    @Override @SuppressWarnings("unchecked")
    public <E extends T> RegistryHolder<E> registerHolder(String name, Supplier<E> entry) {
        ResourceLocation value = ResourceLocation.fromNamespaceAndPath(this.modId, name);
        E registered = Registry.register(this.registry, value, entry.get());
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