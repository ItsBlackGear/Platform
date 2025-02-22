package com.blackgear.platform.core.helper;

import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

import java.util.Optional;

public class DataSerializerRegistry {
    public static DataSerializerRegistry create() {
        return new DataSerializerRegistry();
    }

    public <T> EntityDataSerializer<T> simple(FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
        return register(EntityDataSerializer.simple(writer, reader));
    }

    public <T> EntityDataSerializer<Optional<T>> optional(FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
        return register(EntityDataSerializer.optional(writer, reader));
    }

    public <T extends Enum<T>> EntityDataSerializer<T> simpleEnum(Class<T> clazz) {
        return register(EntityDataSerializer.simpleEnum(clazz));
    }

    public <T> EntityDataSerializer<T> simpleId(IdMap<T> idMap) {
        return register(EntityDataSerializer.simpleId(idMap));
    }

    public <T> EntityDataSerializer<T> register(EntityDataSerializer<T> serializer) {
        EntityDataSerializers.registerSerializer(serializer);
        return serializer;
    }

    public void register() {}
}