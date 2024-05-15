package com.blackgear.platform.core.util.network.server;

import com.blackgear.platform.core.util.network.PacketSender;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.Future;

public class ServerLoginNetworking {
    @ExpectPlatform
    public static boolean registerGlobalReceiver(ResourceLocation channel, LoginQueryResponseHandler handler) {
        throw new AssertionError();
    }
    
    @ExpectPlatform @Nullable
    public static LoginQueryResponseHandler unregisterGlobalReceiver(ResourceLocation channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Set<ResourceLocation> getGlobalReceivers() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static boolean registerReceiver(ServerLoginPacketListenerImpl listener, ResourceLocation channel, LoginQueryResponseHandler handler) {
        throw new AssertionError();
    }
    
    @ExpectPlatform @Nullable
    public static LoginQueryResponseHandler unregisterReceiver(ServerLoginPacketListenerImpl listener, ResourceLocation channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static MinecraftServer getServer(ServerLoginPacketListenerImpl listener) {
        throw new AssertionError();
    }
    
    @FunctionalInterface
    public interface LoginQueryResponseHandler {
        void receive(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buffer, LoginSynchronizer synchronizer, PacketSender sender);
    }
    
    @FunctionalInterface
    public interface LoginSynchronizer {
        void waitFor(Future<?> future);
    }
}