package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.util.EventBus;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CoreRegistryImpl<T> extends CoreRegistry<T> {
    private final DeferredRegister<T> registry;

    protected CoreRegistryImpl(DeferredRegister<T> registry, String modId) {
        super(modId);
        this.registry = registry;
    }

    public static <T> void register(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
        EventBus.get(EventBus.MOD).addListener((Consumer<RegisterEvent>) event -> {
            if (registry.equals(event.getRegistryKey())) {
                source.accept((entry, key) -> event.register(registry, key, () -> entry));
            }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CoreRegistry<T> create(ResourceKey<? extends Registry<T>> key, String modId) {
        return new CoreRegistryImpl(DeferredRegister.create(key, modId), modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        return new CoreRegistryImpl(DeferredRegister.create(registry.key(), modId), modId);
    }

    @Override
    public <E extends T> Supplier<E> register(String name, Supplier<E> entry) {
        return this.registry.register(name, entry);
    }

    @Override
    public <E extends T> Holder<T> holder(String name, Supplier<E> entry) {
        RegistryObject<T> registry = this.registry.register(name, entry);
        return registry.getHolder().orElse(this.registry().wrapAsHolder(entry.get()));
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
        return ((Registry<T>) BuiltInRegistries.REGISTRY).get((ResourceKey<T>) key);
    }

    @Override
    protected void bootstrap() {
        this.registry.register(EventBus.get(EventBus.MOD));
    }
}