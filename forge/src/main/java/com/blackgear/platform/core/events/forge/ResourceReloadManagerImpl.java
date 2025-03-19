package com.blackgear.platform.core.events.forge;

import com.blackgear.platform.core.events.ResourceReloadManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.function.Consumer;

public class ResourceReloadManagerImpl {
    public static void register(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        Consumer<AddReloadListenerEvent> consumer = event -> {
            ResourceReloadManager.ListenerEvent listener = (id, reloadListener) -> event.addListener(reloadListener);
            exporter.accept(listener);
        };
        MinecraftForge.EVENT_BUS.addListener(consumer);
    }
}