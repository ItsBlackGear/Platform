package com.blackgear.platform.core.networking.neoforge;

import com.blackgear.platform.core.networking.PayloadContext;
import com.blackgear.platform.core.networking.Networking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkingImpl {
    public static void register(Consumer<Networking.Registrar> listener) {
        Consumer<RegisterPayloadHandlersEvent> consumer = event -> {
            PayloadRegistrar registrar = event.registrar("1");
            listener.accept(new Networking.Registrar() {
                @Override
                public <T extends CustomPacketPayload> void registerToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PayloadContext> handler) {
                    registrar.playToServer(type, codec, (payload, context) -> handler.accept(payload, wrapper(context)));
                }

                @Override
                public <T extends CustomPacketPayload> void registerToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PayloadContext> handler) {
                    registrar.playToClient(type, codec, (payload, context) -> handler.accept(payload, wrapper(context)));
                }
            });
        };
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener(consumer);
    }

    private static @NotNull PayloadContext wrapper(IPayloadContext context) {
        return new PayloadContext() {
            @Override
            public Player player() {
                return context.player();
            }

            @Override
            public CompletableFuture<Void> enqueueWork(Runnable runnable) {
                return context.enqueueWork(runnable);
            }

            @Override
            public <T> CompletableFuture<T> enqueueWork(Supplier<T> future) {
                return context.enqueueWork(future);
            }
        };
    }
}