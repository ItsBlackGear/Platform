package com.blackgear.platform.core.events.forge;

import com.blackgear.platform.core.events.ResourcePackManager;
import com.blackgear.platform.core.util.EventBus;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;
import java.util.function.Consumer;

public class ResourcePackManagerImpl {
    public static void registerPack(Consumer<ResourcePackManager.Event> listener) {
        EventBus.get(EventBus.MOD).addListener((AddPackFindersEvent event) -> {
            listener.accept((type, source) -> {
                if (event.getPackType() == type) {
                    event.addRepositorySource(source);
                }
            });
        });
    }

    public static void registerBuiltResourcePack(ResourceLocation packId, String modId, String packName) {
        IModFile file = ModList.get().getModFileById(modId).getFile();
        registerPack(event -> {
            event.register(PackType.CLIENT_RESOURCES, () -> {
                return Pack.create(
                    packId.toString(),
                    Component.literal(packName),
                    false,
                    string -> new ModFilePackResources(packName, file, "resourcepacks/" + packId.getPath()),
                    new Pack.Info(
                        Component.literal(modId + " (built-in: " + modId + ")"),
                        SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES),
                        FeatureFlagSet.of()
                    ),
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    false,
                    PackSource.DEFAULT
                );
            });
        });
    }

    private static class ModFilePackResources extends PathPackResources {
        private final IModFile modFile;
        private final String sourcePath;

        public ModFilePackResources(String packId, IModFile modFile, String sourcePath) {
            super(packId, true, modFile.findResource(sourcePath));
            this.modFile = modFile;
            this.sourcePath = sourcePath;
        }

        @Override
        protected Path resolve(String... paths) {
            String[] allPaths = new String[paths.length + 1];
            allPaths[0] = this.sourcePath;
            System.arraycopy(paths, 0, allPaths, 1, paths.length);
            return this.modFile.findResource(allPaths);
        }
    }
}