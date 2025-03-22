package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class CoreRegistryImpl {
    public static <T> CoreRegistry<T> create(ResourceKey<? extends Registry<T>> registry, String modId) {
        return CoreRegistry.FabricatedRegistry.create(registry, modId);
    }

    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        return CoreRegistry.FabricatedRegistry.create(registry, modId);
    }
}