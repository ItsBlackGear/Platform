package com.blackgear.platform.core.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class DataSerializerRegistry {
    @ExpectPlatform
    public static DataSerializerRegistry create(String modId) {
        throw new AssertionError();
    }

    public <T> Supplier<EntityDataSerializer<T>> simple(String name, FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
        return register(name, () -> EntityDataSerializer.simple(writer, reader));
    }

    public <T> Supplier<EntityDataSerializer<Optional<T>>> optional(String name, FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
        return register(name, () -> EntityDataSerializer.optional(writer, reader));
    }

    public <T extends Enum<T>> Supplier<EntityDataSerializer<T>> simpleEnum(String name, Class<T> clazz) {
        return register(name, () -> EntityDataSerializer.simpleEnum(clazz));
    }

    public <T> Supplier<EntityDataSerializer<T>> simpleId(String name, IdMap<T> idMap) {
        return register(name, () -> EntityDataSerializer.simpleId(idMap));
    }

    public abstract <T> Supplier<EntityDataSerializer<T>> register(String name, Supplier<EntityDataSerializer<T>> serializer);

    public abstract void register();
}