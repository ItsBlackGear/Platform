package com.blackgear.platform.core.events.neoforge;

import com.blackgear.platform.core.events.ResourceReloadManager;
import com.blackgear.platform.neoforge.ClientModPipelines;
import com.blackgear.platform.neoforge.CommonLoaderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ResourceReloadManagerImpl {
    @OnlyIn(Dist.CLIENT)
    public static void registerClient(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        ClientModPipelines.RESOURCE_LISTENERS.add(event -> exporter.accept((id, reloadListener) -> event.registerReloadListener(reloadListener)));
    }

    public static void registerServer(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        CommonLoaderPipelines.RESOURCE_LISTENERS.add(event -> exporter.accept((id, reloadListener) -> event.addListener(reloadListener)));
    }
}