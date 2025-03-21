package com.blackgear.platform.core.events.fabric;

import com.blackgear.platform.core.events.ResourceReloadManager;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class ResourceReloadManagerImpl {
    public static void registerClient(Consumer<ResourceReloadManager.ListenerEvent> event) {
        event.accept((id, listener) -> ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(listenerWrapper(id, listener)));
    }

    public static void registerServer(Consumer<ResourceReloadManager.ListenerEvent> event) {
        event.accept((id, listener) -> ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(listenerWrapper(id, listener)));
    }

    private static IdentifiableResourceReloadListener listenerWrapper(ResourceLocation id, PreparableReloadListener listener) {
        return new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return id;
            }

            @Override
            public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        };
    }
}