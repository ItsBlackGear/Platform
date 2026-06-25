package com.blackgear.platform.core.events.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ResourcePackManager;
import com.blackgear.platform.core.mixin.access.PackRepositoryAccessor;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.*;
import java.util.function.Consumer;

public class ResourcePackManagerImpl {
    public static final List<RepositorySource> PACKS = new ArrayList<>();

    public static void registerPack(Consumer<ResourcePackManager.Event> listener) {
        listener.accept((type, source) -> {
            if (Environment.isClientSide() && type == PackType.CLIENT_RESOURCES) {
                if (Minecraft.getInstance().getResourcePackRepository() instanceof PackRepositoryAccessor repository) {
                    Set<RepositorySource> sources = new HashSet<>(repository.getSources());
                    sources.add(source);
                    repository.setSources(sources);
                }
            }

            if (type == PackType.SERVER_DATA) {
                PACKS.add(source);
            }
        });
    }

    public static void registerBuiltResourcePack(ResourceLocation packId, String modId, String packName) {
        ResourceManagerHelper.registerBuiltinResourcePack(
            packId,
            FabricLoader.getInstance().getModContainer(modId).orElseThrow(),
            packName,
            ResourcePackActivationType.NORMAL

        );
    }
}