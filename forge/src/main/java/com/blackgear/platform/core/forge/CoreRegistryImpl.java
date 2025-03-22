package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import java.util.function.Supplier;

public class CoreRegistryImpl<T extends IForgeRegistryEntry<T>> extends CoreRegistry<T> {
    private final DeferredRegister<T> registry;
    private final ResourceKey<? extends Registry<T>> resourceKey;

    protected CoreRegistryImpl(IForgeRegistry<T> entry, ResourceKey<? extends Registry<T>> resourceKey, String modId) {
        super(modId);
        this.resourceKey = resourceKey;
        this.registry = DeferredRegister.create(entry, modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CoreRegistry<T> create(ResourceKey<? extends Registry<T>> key, String modId) {
        IForgeRegistry entry = RegistryManager.ACTIVE.getRegistry((ResourceKey) key);
        return entry != null ? new CoreRegistryImpl(entry, key, modId) : FabricatedRegistry.create(key, modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        IForgeRegistry entry = RegistryManager.ACTIVE.getRegistry((ResourceKey) registry.key());
        return entry != null ? new CoreRegistryImpl(entry, registry.key(), modId) : FabricatedRegistry.create(registry, modId);
    }

    @Override @SuppressWarnings("unchecked")
    public <E extends T> Supplier<E> register(String name, Supplier<E> entry) {
        RegistryObject<E> value = this.registry.register(name, entry);
        this.entries.add((Supplier<T>) value);
        return value;
    }

    @Override @SuppressWarnings("unchecked")
    public <E extends T> E vanilla(String name, E entry) {
        RegistryObject<E> value = this.registry.register(name, () -> entry);
        this.entries.add((Supplier<T>) value);
        return entry;
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return this.resourceKey;
    }

    @Override
    public Registry<T> registry() {
        return get(key());
    }

    @SuppressWarnings("unchecked")
    private static <T> T get(ResourceKey<?> key) {
        return ((Registry<T>) Registry.REGISTRY).get((ResourceKey<T>) key);
    }

    @Override
    protected void bootstrap() {
        this.registry.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}