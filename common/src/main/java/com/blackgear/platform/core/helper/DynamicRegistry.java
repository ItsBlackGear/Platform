package com.blackgear.platform.core.helper;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;

public class DynamicRegistry {
    @ExpectPlatform
    public static void onRegister(Consumer<Registrar> consumer) {
    }

    public interface Registrar {
        <T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec);
    }
}