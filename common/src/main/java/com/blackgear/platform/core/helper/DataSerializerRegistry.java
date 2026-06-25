package com.blackgear.platform.core.helper;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.function.Supplier;

public abstract class DataSerializerRegistry {
    @ExpectPlatform
    public static DataSerializerRegistry create(String modId) {
        throw new AssertionError();
    }

    public <T> Supplier<EntityDataSerializer<T>> register(String name, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        return register(name, () -> EntityDataSerializer.forValueType(codec));
    }

    public abstract <T> Supplier<EntityDataSerializer<T>> register(String name, Supplier<EntityDataSerializer<T>> serializer);

    public abstract void register();
}