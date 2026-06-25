package com.blackgear.platform.core.api.registrar.bootstrap;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BootstrapRegistrar<T> {
    protected final LinkedHashMap<ResourceKey<T>, Function<BootstapContext<T>, T>> entries = new LinkedHashMap<>();
    protected final ResourceKey<? extends Registry<T>> registry;
    protected final String modId;

    BootstrapRegistrar(ResourceKey<? extends Registry<T>> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    public static <T> BootstrapRegistrar<T> create(ResourceKey<? extends Registry<T>> registry, String modId) {
        return new BootstrapRegistrar<>(registry, modId);
    }
    
    public ResourceKey<T> register(String name, Function<BootstapContext<T>, T> factory) {
        ResourceKey<T> key = ResourceKey.create(this.registry, new ResourceLocation(this.modId, name));
        this.entries.put(key, factory);
        return key;
    }
    
    public ResourceKey<T> resource(String name, BiFunction<BootstapContext<T>, ResourceLocation, T> factory) {
        return this.register(name, (context) -> factory.apply(context, new ResourceLocation(this.modId, name)));
    }

    public void bootstrap(BootstapContext<T> context) {
        this.entries.forEach((key, factory) -> context.register(key, factory.apply(context)));
    }
}