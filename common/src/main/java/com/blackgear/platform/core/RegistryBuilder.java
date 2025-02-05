package com.blackgear.platform.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Utility class to help registering new registries.
 *
 * <p>Example of the registry of a registry.</p>
 *
 * <pre>{@code
 *
 * // Create a new instance of the RegistryBuilder.
 * RegistryBuilder BUILDER = new RegistryBuilder(MOD_ID);
 *
 * // Register your new registry entry, in this case, an IntProviderType.
 *
 * RegistryBuilder.Sample<IntProviderType<?>> INT_PROVIDER_TYPE = BUILDER.create(
 * 	"int_provider_type",
 * 	registry -> IntProviderType.CONSTANT
 * );
 *
 * }</pre>
 *
 * This type of registry can later be used to create registries using {@link CoreRegistry}.
 *
 * <pre>{@code
 *
 * CoreRegistry<IntProviderType<?>> INT_PROVIDERS = CoreRegistry.create(
 *     INT_PROVIDER_TYPE.registry(),
 *     MOD_ID
 * );
 *
 * }</pre>
 *
 * By using {@link Sample} you can also call either the {@link ResourceKey} or the {@link Registry}.
 *
 * @author ItsBlackGear
 */
public record RegistryBuilder(String modId) {
    public <T> Sample<T> create(String key, Registry.RegistryBootstrap<T> bootstrap) {
        ResourceKey<Registry<T>> resource = ResourceKey.createRegistryKey(new ResourceLocation(this.modId, key));
        return new Sample<>(resource, Registry.registerSimple(resource, bootstrap));
    }
    
    public static void bootstrap() {}
    
    public record Sample<T>(ResourceKey<Registry<T>> resource, Registry<T> registry) {}
}