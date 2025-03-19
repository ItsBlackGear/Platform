package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

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

    @Override @SuppressWarnings("unchecked")
    public <E extends T> Supplier<E> register(String name, Supplier<E> entry) {
        RegistryObject<E> value = this.registry.register(name, entry);
        this.entries.add((Supplier<T>) value);
        return value;
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
        return ((Registry<T>) Registry.REGISTRY).get((ResourceKey<T>) key);
    }

    @Override
    protected void bootstrap() {
        this.registry.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}