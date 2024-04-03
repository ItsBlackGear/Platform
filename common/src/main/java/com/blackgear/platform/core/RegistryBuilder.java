package com.blackgear.platform.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record RegistryBuilder(String modId) {
    public <T> Sample<T> create(String key, Registry.RegistryBootstrap<T> bootstrap) {
        ResourceKey<Registry<T>> resource = ResourceKey.createRegistryKey(new ResourceLocation(this.modId, key));
        return new Sample<>(resource, Registry.registerSimple(resource, bootstrap));
    }

    public static void bootstrap() {}

    public record Sample<T>(ResourceKey<Registry<T>> resource, Registry<T> registry) {}
}