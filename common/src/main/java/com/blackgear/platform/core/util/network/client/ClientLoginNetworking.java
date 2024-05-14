package com.blackgear.platform.core.util.network.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ClientLoginNetworking {
    @ExpectPlatform
    public static boolean registerGlobalReceiver(ResourceLocation channel, LoginQueryRequestHandler handler) {
        throw new AssertionError();
    }
    
    @ExpectPlatform @Nullable
    public static LoginQueryRequestHandler unregisterGlobalReceiver(ResourceLocation channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Set<ResourceLocation> getGlobalReceivers() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static boolean registerReceiver(ResourceLocation channel, LoginQueryRequestHandler handler) throws IllegalStateException {
        throw new AssertionError();
    }
    
    @ExpectPlatform @Nullable
    public static LoginQueryRequestHandler unregisterReceiver(ResourceLocation channel) throws IllegalStateException {
        throw new AssertionError();
    }
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface LoginQueryRequestHandler {
        CompletableFuture<@Nullable FriendlyByteBuf> receive(Minecraft client, ClientHandshakePacketListenerImpl listener, FriendlyByteBuf buffer, Consumer<GenericFutureListener<? extends Future<? super Void>>> exporter);
    }
}