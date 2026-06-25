package com.blackgear.platform.core.events.neoforge;

import com.blackgear.platform.core.events.ResourceReloadManager;
import com.blackgear.platform.core.util.EventBus;
import com.blackgear.platform.neoforge.client.ForgeClientEventHandler;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.function.Consumer;

public class ResourceReloadManagerImpl {
    public static void registerClient(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        ForgeClientEventHandler.registerClientResourceListeners(exporter);
    }

    public static void registerServer(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        EventBus.get(EventBus.LOADER).addListener((AddReloadListenerEvent event) -> {
            ResourceReloadManager.ListenerEvent listener = (id, reloadListener) -> event.addListener(reloadListener);
            exporter.accept(listener);
        });
    }
}