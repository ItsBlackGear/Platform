package com.blackgear.platform.core.events;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ResourcePackManager {
    @ExpectPlatform
    public static void registerPack(Consumer<Event> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerBuiltResourcePack(ResourceLocation packId, String modId, String packName) {
        throw new AssertionError();
    }

    public interface Event {
        void register(PackType packType, RepositorySource pack);

        default void register(PackType packType, Supplier<Pack> supplier) {
            if (supplier == null) return;
            register(packType, loader -> {
                Pack pack = supplier.get();
                if (pack != null) loader.accept(pack);
            });
        }
    }
}