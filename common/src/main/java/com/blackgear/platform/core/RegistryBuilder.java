package com.blackgear.platform.core;

import dev.architectury.injectables.annotations.ExpectPlatform;
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
public abstract class RegistryBuilder {
    protected final String modId;

    protected RegistryBuilder(String modId) {
        this.modId = modId;
    }

    @ExpectPlatform
    public static RegistryBuilder create(String modId) {
        throw new AssertionError();
    }

    /**
     * Creates a ResourceKey for a registry with the given name.
     *
     * @param name the registry name
     * @return a ResourceKey for the registry
     */
    public <T> ResourceKey<Registry<T>> resource(String name) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(this.modId, name));
    }

    /**
     * Registers a simple registry with the given key and bootstrap function.
     *
     * @param key the registry key
     * @return the created registry
     */
    public abstract <T> Supplier<Registry<T>> registry(ResourceKey<Registry<T>> key);

    /**
     * Creates and registers a new registry type.
     *
     * @param name the registry name
     * @param <T> the type of registry being created
     * @return a RegistryReference containing the registry key and registry
     * @throws NullPointerException if key or bootstrap is null
     */
    public <T> RegistryReference<T> reference(String name) {
        Objects.requireNonNull(name, "Registry name cannot be null");

        ResourceKey<Registry<T>> resource = this.resource(name);
        return new RegistryReference<>(resource, this.registry(resource));
    }

    /**
     * Performs necessary initialization.
     * This method is intended to be called during mod initialization.
     */
    public static void bootstrap() {}

    /**
     * A reference to a registered registry containing both its ResourceKey and Registry instance.
     *
     * @param <T> the type of registry
     */
    public record RegistryReference<T>(ResourceKey<Registry<T>> resource, Supplier<Registry<T>> registry) {
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