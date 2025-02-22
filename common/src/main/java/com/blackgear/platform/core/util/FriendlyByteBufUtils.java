package com.blackgear.platform.core.util;

import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FriendlyByteBufUtils {
    public static <T> void writeId(FriendlyByteBuf buf, IdMap<T> map, T value) {
        int i = map.getId(value);
        if (i == -1) {
            throw new IllegalArgumentException("Can't find id for '" + value + "' in map " + map);
        } else {
            buf.writeVarInt(i);
        }
    }

    @Nullable
    public static <T> T readById(FriendlyByteBuf buf, IdMap<T> map) {
        return map.byId(buf.readVarInt());
    }

    public static  <T> void writeOptional(FriendlyByteBuf buf, Optional<T> optional, Writer<T> writer) {
        if (optional.isPresent()) {
            buf.writeBoolean(true);
            writer.accept(buf, optional.get());
        } else {
            buf.writeBoolean(false);
        }
    }

    public static <T> Optional<T> readOptional(FriendlyByteBuf buf, Reader<T> reader) {
        return buf.readBoolean() ? Optional.of(reader.apply(buf)) : Optional.empty();
    }

    public interface Reader<T> extends Function<FriendlyByteBuf, T> {
        default Reader<Optional<T>> asOptional() {
            return buf -> readOptional(buf, this);
        }
    }

    public interface Writer<T> extends BiConsumer<FriendlyByteBuf, T> {
        default Writer<Optional<T>> asOptional() {
            return (buf, optional) -> writeOptional(buf, optional, this);
        }
    }
}