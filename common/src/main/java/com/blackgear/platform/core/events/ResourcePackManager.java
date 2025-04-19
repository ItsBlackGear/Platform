package com.blackgear.platform.core.events;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ResourcePackManager {
    @ExpectPlatform
    public static void registerPack(Consumer<Event> listener) {
        throw new AssertionError();
    }

    public interface Event {
        void register(PackType packType, @Nullable Supplier<Pack> pack);
    }
}