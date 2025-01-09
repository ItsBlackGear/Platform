package com.blackgear.platform.core;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Utility class to create registries slightly based on suppliers
 *
 * @author ItsBlackGear
 */
public abstract class CoreRegistry<T> {
    protected final Registry<T> registry;
    protected final String modId;
    protected boolean isPresent = false;

    protected CoreRegistry(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    /**
     * Create a new instance of the CoreRegistry.
     *
     * <p>Example of the creation of a CoreRegistry</p>
     *
     * <pre>{@code
     *
     * // <Item> is the type of the registry
     * CoreRegistry<Item> ITEMS = CoreRegistry.create(
     *     // Registry Type
     *     Registry.ITEM,
     *     // Mod ID
     *     MOD_ID
     * );
     *
     * }</pre>
     */
    @ExpectPlatform
    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        throw new AssertionError();
    }

    /**
     * Registers an entry into the CoreRegistry
     *
     * <p>Example of the registration of an item</p>
     *
     * <pre>{@code
     *
     * CoreRegistry<Biome> ITEMS = CoreRegistry.create(
     *     Registry.ITEM,
     *     MOD_ID
     * );
     *
     * // Registry with Supplier
     * Supplier<Item> CUSTOM_ITEM = ITEMS.register(
     *     "custom_item",
     *     () -> new Item(new Item.Properties())
     * );
     *
     * // Registry without Supplier
     * // There is not much difference between the two,
     * // at this point is just a matter of preference.
     * Item CUSTOM_ITEM = ITEMS.registerUnsafe(
     *     "custom_item",
     *     new Item(new Item.Properties())
     * );
     *
     * }</pre>
     */
    public abstract <E extends T> Supplier<E> register(String key, Supplier<E> entry);
    
    public abstract <E extends T> E registerVanilla(String key, E entry);
    
    /**
     * Registers an entry into the CoreRegistry, returning a ResourceKey
     *
     * <p>Example of the registration of a biome</p>
     *
     * <pre>{@code
     *
     * CoreRegistry<Biome> BIOMES = CoreRegistry.create(
     *     BuiltinRegistries.BIOME,
     *     MOD_ID
     * );
     *
     * ResourceKey<Biome> CUSTOM_BIOME = BIOMES.resource(
     *     "custom_biome",
     *     OverworldBiomes::theVoid
     * );
     *
     * }</pre>
     *
     * It's recommended to be used on entries that require a {@link ResourceKey}, features such as Biomes, Points of Interest, Instruments, etc.
     */
    public <E extends T> ResourceKey<T> resource(String key, Supplier<E> entry) {
        this.register(key, entry);
        return ResourceKey.create(this.registry.key(), new ResourceLocation(this.modId, key));
    }
    
    public <E extends T> ResourceKey<T> resourceVanilla(String key, E entry) {
        this.registerVanilla(key, entry);
        return ResourceKey.create(this.registry.key(), new ResourceLocation(this.modId, key));
    }
    
    /**
     * Initializes the CoreRegistry at the Mod Initializer.
     *
     * <p>Example of the Initialization of the CoreRegistry</p>
     *
     * <pre>{@code
     *
     * ModItems.ITEMS.register();
     * ModBiomes.BIOMES.register();
     *
     * }</pre>
     *
     * The CoreRegistry must be initialized at the main class, otherwise it may not load in time for forge features.
     */
    public void register() {
        if (this.isPresent) {
            throw new IllegalArgumentException("Duplication of Registry: " + this.registry);
        }
        
        this.isPresent = true;
        this.bootstrap();
    }

    protected abstract void bootstrap();
    
    public static class SimpleRegistry<T> extends CoreRegistry<T> {
        public SimpleRegistry(Registry<T> registry, String modId) {
            super(registry, modId);
        }
        
        @Override
        public <E extends T> Supplier<E> register(String key, Supplier<E> entry) {
            E value = Registry.register(this.registry, new ResourceLocation(this.modId, key), entry.get());
            return () -> value;
        }
        
        @Override
        public <E extends T> E registerVanilla(String key, E entry) {
            return Registry.register(this.registry, new ResourceLocation(this.modId, key), entry);
        }
        
        @Override
        protected void bootstrap() {}
    }
}