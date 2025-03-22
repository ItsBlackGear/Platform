package com.blackgear.platform.core.events.forge;

import com.blackgear.platform.core.events.ResourceReloadManager;
import com.blackgear.platform.forge.ForgeClientEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.function.Consumer;

public class ResourceReloadManagerImpl {
    public static void registerClient(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        ForgeClientEventHandler.registerClientResourceListeners(exporter);
    }

    public static void registerServer(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        Consumer<AddReloadListenerEvent> consumer = event -> {
            ResourceReloadManager.ListenerEvent listener = (id, reloadListener) -> event.addListener(reloadListener);
            exporter.accept(listener);
        };
        MinecraftForge.EVENT_BUS.addListener(consumer);
    }
}