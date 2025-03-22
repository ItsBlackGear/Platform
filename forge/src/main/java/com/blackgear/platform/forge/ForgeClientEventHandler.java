package com.blackgear.platform.forge;

import com.blackgear.platform.core.events.ResourceReloadManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.function.Consumer;

// TODO: migrate all forge client events here
// Store Client side event handlers to prevent invalid dist crashes on dedicated servers
public class ForgeClientEventHandler {
    public static void registerClientResourceListeners(Consumer<ResourceReloadManager.ListenerEvent> exporter) {
        exporter.accept((id, listener) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null) return;

            ResourceManager resourceManager = minecraft.getResourceManager();
            if (resourceManager instanceof ReloadableResourceManager) {
                ((ReloadableResourceManager) resourceManager).registerReloadListener(listener);
            }
        });
    }
}