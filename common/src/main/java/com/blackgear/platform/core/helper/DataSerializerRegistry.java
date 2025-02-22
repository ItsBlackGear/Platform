package com.blackgear.platform.core.helper;

import com.blackgear.platform.core.util.FriendlyByteBufUtils;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

import java.util.Optional;

public class DataSerializerRegistry {
    public static DataSerializerRegistry create() {
        return new DataSerializerRegistry();
    }

    public <T> EntityDataSerializer<T> simple(FriendlyByteBufUtils.Writer<T> writer, FriendlyByteBufUtils.Reader<T> reader) {
        return register(new ForValueType<T>() {
            @Override
            public void write(FriendlyByteBuf buffer, T value) {
                writer.accept(buffer, value);
            }

            @Override
            public T read(FriendlyByteBuf buffer) {
                    return reader.apply(buffer);
                }
        });
    }

    public <T> EntityDataSerializer<Optional<T>> optional(FriendlyByteBufUtils.Writer<T> writer, FriendlyByteBufUtils.Reader<T> reader) {
        return simple(writer.asOptional(), reader.asOptional());
    }

    public <T extends Enum<T>> EntityDataSerializer<T> simpleEnum(Class<T> clazz) {
        return simple(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(clazz));
    }

    public <T> EntityDataSerializer<T> simpleId(IdMap<T> idMap) {
        return simple((buf, arg) -> FriendlyByteBufUtils.writeId(buf, idMap, arg), buf -> FriendlyByteBufUtils.readById(buf, idMap));
    }

    public <T> EntityDataSerializer<T> register(EntityDataSerializer<T> serializer) {
        EntityDataSerializers.registerSerializer(serializer);
        return serializer;
    }

    public void register() {}

    private interface ForValueType<T> extends EntityDataSerializer<T> {
        @Override default T copy(T value) {
            return value;
        }
    }
}