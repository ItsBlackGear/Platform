package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CoreRegistryImpl<T> extends CoreRegistry<T> {
    public static final Set<ResourceKey<?>> REGISTERED_KEYS = ConcurrentHashMap.newKeySet();
    private final Registry<T> registry;

    protected CoreRegistryImpl(Registry<T> registry, String modId) {
        super(modId);
        this.registry = registry;
    }

    public static <T> void register(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
        source.accept((entry, key) -> {
            ResourceKey<T> resourceKey = ResourceKey.create(registry, key);
            if (key.getNamespace().equals("minecraft")) REGISTERED_KEYS.add(resourceKey);

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
        ResourceLocation location = new ResourceLocation(this.modId, name);
        ResourceKey<T> resourceKey = ResourceKey.create(this.registry.key(), location);

        REGISTERED_KEYS.add(resourceKey);

        E value = Registry.register(this.registry, location, entry.get());
        return () -> value;
    }

    @Override
    public <E extends T> Holder<T> holder(String name, Supplier<E> entry) {
        ResourceLocation location = new ResourceLocation(this.modId, name);
        ResourceKey<T> resourceKey = ResourceKey.create(this.registry.key(), location);

        if (this.modId.equals("minecraft")) REGISTERED_KEYS.add(resourceKey);

        return Registry.registerForHolder(this.registry, location, entry.get());
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