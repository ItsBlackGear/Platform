package com.blackgear.platform.core.events.forge;

import com.blackgear.platform.core.events.ResourcePackManager;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;

public class ResourcePackManagerImpl {
    public static void registerPack(Consumer<ResourcePackManager.Event> listener) {
        Consumer<AddPackFindersEvent> consumer = event -> listener.accept((packType, pack) -> {
            if (pack == null) return;

            if (event.getPackType() == packType && pack.get() != null) {
                event.addRepositorySource((onLoad, factory) -> onLoad.accept(pack.get()));
            }
        });
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }
}