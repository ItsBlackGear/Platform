package com.blackgear.platform.core.events.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ResourcePackManager;
import com.blackgear.platform.core.mixin.access.PackRepositoryAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ResourcePackManagerImpl {
    private static final Map<PackType, List<Supplier<Pack>>> PACKS = new EnumMap<>(PackType.class);

    public static void registerPack(Consumer<ResourcePackManager.Event> listener) {
        listener.accept((packType, pack) -> {
            if (pack == null) return;
            PACKS.computeIfAbsent(packType, p -> new ArrayList<>()).add(pack);

            // Update client resource repository immediately if applicable
            if (Environment.isClientSide() && packType == PackType.CLIENT_RESOURCES) {
                updateClientRepository();
            }
        });
    }

    private static void updateClientRepository() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getResourcePackRepository() instanceof PackRepositoryAccessor repository) {
            Set<RepositorySource> sources = new HashSet<>(repository.getSources());
            getAdditionalPacks(PackType.CLIENT_RESOURCES)
                .forEach(pack -> sources.add(onLoad -> onLoad.accept(pack.get())));
            repository.setSources(sources);
        }
    }

    public static Collection<Supplier<Pack>> getAdditionalPacks(@Nullable PackType packType) {
        List<Supplier<Pack>> result = PACKS.get(packType);
        return result != null ? result : Collections.emptyList();
    }
}