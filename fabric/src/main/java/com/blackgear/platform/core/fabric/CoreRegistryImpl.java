package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;

public class CoreRegistryImpl {
    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        return new CoreRegistry.SimpleRegistry<>(registry, modId);
    }
}