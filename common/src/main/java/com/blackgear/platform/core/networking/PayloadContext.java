package com.blackgear.platform.core.networking;

import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface PayloadContext {
    Player player();

    CompletableFuture<Void> enqueueWork(Runnable runnable);

    <T> CompletableFuture<T> enqueueWork(Supplier<T> future);
}