package com.blackgear.platform.core.networking.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.networking.PayloadContext;
import com.blackgear.platform.core.networking.Networking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkingImpl {
    public static void register(Consumer<Networking.Registrar> listener) {
        listener.accept(new Networking.Registrar() {
            @Override
            public <T extends CustomPacketPayload> void registerToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PayloadContext> handler) {
                PayloadTypeRegistry.playC2S().register(type, codec);
                ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(payload, serverboundWrapper(context)));
            }

            @Override
            public <T extends CustomPacketPayload> void registerToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PayloadContext> handler) {
                if (Environment.isClientSide()) ClientNetworking.registerToClient(type, codec, handler);
            }
        });
    }

    private static PayloadContext serverboundWrapper(ServerPlayNetworking.Context context) {
        return new PayloadContext() {
            @Override
            public Player player() {
                return context.player();
            }

            @Override
            public CompletableFuture<Void> enqueueWork(Runnable runnable) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                context.server().execute(() -> {
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
                context.server().execute(() -> {
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
