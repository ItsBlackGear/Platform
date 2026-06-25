package com.blackgear.platform.core.networking.fabric;

import com.blackgear.platform.core.networking.PayloadContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ClientNetworking {
    public static <T extends CustomPacketPayload> void registerToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PayloadContext> handler) {
        PayloadTypeRegistry.playS2C().register(type, codec);
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(payload, clientboundWrapper(context)));
    }

    private static PayloadContext clientboundWrapper(ClientPlayNetworking.Context context) {
        return new PayloadContext() {
            @Override
            public Player player() {
                return context.player();
            }

            @Override
            public CompletableFuture<Void> enqueueWork(Runnable runnable) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                context.client().execute(() -> {
                    try {
                        runnable.run();
                        future.complete(null);
                    } catch (Exception exception) {
                        future.completeExceptionally(exception);
                    }
                });
                return future;
            }

            @Override
            public <S> CompletableFuture<S> enqueueWork(Supplier<S> supplier) {
                CompletableFuture<S> future = new CompletableFuture<>();
                context.client().execute(() -> {
                    try {
                        future.complete(supplier.get());
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                });
                return future;
            }
        };
    }
}
