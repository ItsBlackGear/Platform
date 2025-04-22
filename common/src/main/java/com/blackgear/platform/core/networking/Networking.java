package com.blackgear.platform.core.networking;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Networking {
    @ExpectPlatform
    public static void register(Consumer<Registrar> listener) {
        throw new AssertionError();
    }

    public interface Registrar {
        <T extends CustomPacketPayload> void registerToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PayloadContext> handler);

        <T extends CustomPacketPayload> void registerToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PayloadContext> handler);

        default <T extends CustomPacketPayload> void registerBidirectional(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PayloadContext> handler) {
            registerToServer(type, codec, handler);
            registerToClient(type, codec, handler);
        }
    }
}