package com.blackgear.platform.core.helper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class DataSerializerRegistry {
    public static DataSerializerRegistry create() {
        return new DataSerializerRegistry();
    }

    public <T> EntityDataSerializer<T> create(StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        return register(EntityDataSerializer.forValueType(codec));
    }

    public <T> EntityDataSerializer<T> register(EntityDataSerializer<T> serializer) {
        EntityDataSerializers.registerSerializer(serializer);
        return serializer;
    }

    public void register() {}
}