package com.blackgear.platform.forge;

import com.blackgear.platform.core.events.ResourceReloadManager;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;

// TODO: migrate all forge common events here
// Store Client side event handlers to prevent invalid dist crashes on dedicated servers
public class ForgeClientEventHandler {
    public static void registerClientResourceListeners(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        Consumer<RegisterClientReloadListenersEvent> consumer = event -> {
            ResourceReloadManager.ListenerEvent listener = (id, reloadListener) -> event.registerReloadListener(reloadListener);
            exporter.accept(listener);
        };
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }
}