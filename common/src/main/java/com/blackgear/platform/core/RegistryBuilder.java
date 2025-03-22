package com.blackgear.platform.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Utility class to help register custom registries.
 * <p>
 * This class simplifies the process of creating new registry types that can
 * later be populated with entries using {@link CoreRegistry}.
 *
 * <p><b>Example:</b> Creating a new registry type for IntProviderType</p>
 *
 * <pre>{@code
 * // Create a new registry builder for your mod
 * RegistryBuilder BUILDER = new RegistryBuilder(MOD_ID);
 *
 * // Register a new registry type
 * RegistryBuilder.RegistryReference<IntProviderType<?>> INT_PROVIDER_TYPE = BUILDER.create(
 *     "int_provider_type",
 *     registry -> IntProviderType.CONSTANT
 * );
 *
 * // Later use this registry with CoreRegistry
 * CoreRegistry<IntProviderType<?>> INT_PROVIDERS = CoreRegistry.create(
 *     INT_PROVIDER_TYPE.registry(),
 *     MOD_ID
 * );
 * }</pre>
 */
public final class RegistryBuilder {
    private final String modId;
    
    public RegistryBuilder(String modId) {
        this.modId = modId;
    }

    /**
     * Creates a ResourceKey for a registry with the given name.
     *
     * @param name the registry name
     * @return a ResourceKey for the registry
     */
    public <T> ResourceKey<Registry<T>> resource(String name) {
        return ResourceKey.createRegistryKey(new ResourceLocation(this.modId, name));
    }

    /**
     * Registers a simple registry with the given key and bootstrap function.
     *
     * @param key the registry key
     * @param bootstrap the bootstrap function
     * @return the created registry
     */
    public <T> Registry<T> registry(ResourceKey<Registry<T>> key, Supplier<T> bootstrap) {
        return Registry.registerSimple(key, bootstrap);
    }

    /**
     * Creates and registers a new registry type.
     *
     * @param name the registry name
     * @param bootstrap the bootstrap function that provides the default registry value
     * @param <T> the type of registry being created
     * @return a RegistryReference containing the registry key and registry
     * @throws NullPointerException if key or bootstrap is null
     */
    public <T> RegistryReference<T> create(String name, Supplier<T> bootstrap) {
        Objects.requireNonNull(name, "Registry name cannot be null");
        Objects.requireNonNull(bootstrap, "Bootstrap function cannot be null");

        ResourceKey<Registry<T>> resource = resource(name);
        return new RegistryReference<>(resource, registry(resource, bootstrap));
    }

    /**
     * Performs necessary initialization.
     * This method is intended to be called during mod initialization.
     */
    public void bootstrap() {}

    /**
     * A reference to a registered registry containing both its ResourceKey and Registry instance.
     *
     * @param <T> the type of registry
     */
    public static class RegistryReference<T> {
        private final ResourceKey<Registry<T>> resource;
        private final Registry<T> registry;
        
        public RegistryReference(ResourceKey<Registry<T>> resource, Registry<T> registry) {
            this.resource = resource;
            this.registry = registry;
        }
        
        public ResourceKey<Registry<T>> getResource() {
            return this.resource;
        }
        
        public Registry<T> getRegistry() {
            return this.registry;
        }

        /**
         * Gets the resource location for this registry.
         *
         * @return the ResourceLocation representing this registry
         */
        public ResourceLocation location() {
            return this.resource.location();
        }
    }
}