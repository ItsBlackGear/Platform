package com.blackgear.platform.neoforge.client;

import com.blackgear.platform.core.events.ResourceReloadManager;
import com.blackgear.platform.core.util.EventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.util.function.Consumer;

// Store Client side event handlers to prevent invalid dist crashes on dedicated servers
public class ForgeClientEventHandler {
    public static void registerClientResourceListeners(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        EventBus.get(EventBus.MOD).addListener((RegisterClientReloadListenersEvent event) -> exporter.accept((id, reloadListener) -> event.registerReloadListener(reloadListener)));
    }
}