package com.blackgear.platform.core;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A platform-agnostic registry wrapper providing consistent registration across Forge and Fabric.
 * @param <T> The type of objects being registered
 */
public abstract class CoreRegistry<T> {
    protected final String modId;
    protected boolean isPresent = false;

    protected final Set<Supplier<T>> entries = new HashSet<>();

    protected CoreRegistry(String modId) {
        this.modId = modId;
    }

    /**
     * Creates a registry for the given registry key and mod ID.
     * @param key The registry key
     * @param modId The mod ID
     * @return A platform-specific registry implementation
     */
    @ExpectPlatform
    public static <T> CoreRegistry<T> create(ResourceKey<? extends Registry<T>> key, String modId) {
        throw new AssertionError();
    }

    /**
     * Creates a registry for the given registry and mod ID.
     * @param registry The registry
     * @param modId The mod ID
     * @return A platform-specific registry implementation
     */
    @ExpectPlatform
    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        throw new AssertionError();
    }

    /**
     * Registers an entry with the given name.
     * @param name The registry name
     * @param entry The entry supplier
     * @return A supplier for the registered entry
     */
    public abstract <E extends T> Supplier<E> register(String name, Supplier<E> entry);

    /**
     * Registers an entry and returns its ResourceKey.
     * @param name The registry name
     * @param entry The entry supplier
     * @return The ResourceKey for the registered entry
     */
    public <E extends T> ResourceKey<T> resource(String name, Supplier<E> entry) {
        this.register(name, entry);
        return ResourceKey.create(this.key(), new ResourceLocation(this.modId, name));
    }

    /**
     * @return All registered entries
     */
    public Collection<Supplier<T>> entries() {
        return Collections.unmodifiableSet(this.entries);
    }

    /**
     * @return The registry key
     */
    public abstract ResourceKey<? extends Registry<T>> key();

    /**
     * @return The underlying Minecraft registry
     */
    public abstract Registry<T> registry();

    /**
     * @return The mod ID
     */
    public String modId() {
        return this.modId;
    }

    /**
     * Registers this registry with the platform.
     * Throws if called multiple times.
     */
    public void register() {
        if (this.isPresent) {
            throw new IllegalArgumentException("Duplication of Registry: " + this.key());
        }
        
        this.isPresent = true;
        this.bootstrap();
    }

    /**
     * Platform-specific bootstrap logic
     */
    protected abstract void bootstrap();
}