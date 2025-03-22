package com.blackgear.platform.core.events;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public class ResourceReloadManager {
    @ExpectPlatform
    public static void registerClient(Consumer<ListenerEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerServer(Consumer<ListenerEvent> listener) {
        throw new AssertionError();
    }

    public interface ListenerEvent {
        void register(ResourceLocation id, PreparableReloadListener consumer);
    }
}