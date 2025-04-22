package com.blackgear.platform.neoforge;

import com.blackgear.platform.core.events.ResourceReloadManager;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.util.function.Consumer;

// Store Client side event handlers to prevent invalid dist crashes on dedicated servers
public class ForgeClientEventHandler {
    public static void registerClientResourceListeners(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        Consumer<RegisterClientReloadListenersEvent> consumer = event -> {
            ResourceReloadManager.ListenerEvent listener = (id, reloadListener) -> event.registerReloadListener(reloadListener);
            exporter.accept(listener);
        };
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener(consumer);
    }
}