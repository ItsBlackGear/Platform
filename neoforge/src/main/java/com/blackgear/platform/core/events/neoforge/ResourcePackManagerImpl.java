package com.blackgear.platform.core.events.neoforge;

import com.blackgear.platform.core.events.ResourcePackManager;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.function.Consumer;

public class ResourcePackManagerImpl {
    public static void registerPack(Consumer<ResourcePackManager.Event> listener) {
        Consumer<AddPackFindersEvent> consumer = event -> listener.accept((packType, pack) -> {
            if (pack == null) return;

            if (event.getPackType() == packType && pack.get() != null) {
                event.addRepositorySource(onLoad -> onLoad.accept(pack.get()));
            }
        });
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener(consumer);
    }
}