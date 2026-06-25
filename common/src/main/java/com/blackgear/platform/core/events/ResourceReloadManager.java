package com.blackgear.platform.core.events;

import com.blackgear.platform.core.BuiltInCoreRegistry;
import com.blackgear.platform.core.api.registrar.resource.BuiltInRegistryReloadListener;
import com.mojang.serialization.Codec;
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
        
        default <T> void register(ResourceLocation id, BuiltInCoreRegistry<T> registry, Codec<T> codec) {
            this.register(id, BuiltInRegistryReloadListener.create(registry, codec, id.getPath()));
        }
    }
}