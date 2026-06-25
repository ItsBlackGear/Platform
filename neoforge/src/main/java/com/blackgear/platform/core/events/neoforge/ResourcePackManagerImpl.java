package com.blackgear.platform.core.events.neoforge;

import com.blackgear.platform.core.events.ResourcePackManager;
import com.blackgear.platform.core.util.EventBus;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ResourcePackManagerImpl {
    public static void registerPack(Consumer<ResourcePackManager.Event> listener) {
        EventBus.get(EventBus.MOD).addListener((Consumer<AddPackFindersEvent>) event ->
            listener.accept((type, source) -> {
                if (event.getPackType() == type) {
                    event.addRepositorySource(source);
                }
            }
        ));
    }

    public static void registerBuiltResourcePack(ResourceLocation packId, String modId, String packName) {
        IModFile file = ModList.get().getModFileById(modId).getFile();
        registerPack(event -> event.register(PackType.CLIENT_RESOURCES, () ->
            Pack.readMetaAndCreate(
                new PackLocationInfo(
                    packId.toString(),
                    Component.literal(packName),
                    PackSource.DEFAULT,
                    Optional.of(new KnownPack(modId, packName, SharedConstants.getCurrentVersion().getId()))
                ),
                new ModFilePackResources.ModPathResourcesSupplier(
                    file,
                    Path.of("resourcepacks/" + packId.getPath())
                ),
                PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(
                    false,
                    Pack.Position.TOP,
                    false
                )
            )
        ));
    }

    private static class ModFilePackResources extends PathPackResources {
        private final PackMetadataSection section;

        public ModFilePackResources(PackLocationInfo packId, IModFile modFile, String sourcePath) {
            super(packId, modFile.findResource(sourcePath));
            this.section = new PackMetadataSection(packId.title(), DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES), Optional.empty());
        }

        @Override @Nullable
        public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
            IoSupplier<InputStream> supplier = this.getRootResource("pack.mcmeta");
            if (supplier == null) {
                return deserializer == PackMetadataSection.TYPE ? (T) this.section : null;
            } else {
                try (InputStream stream = supplier.get()) {
                    return getMetadataFromStream(deserializer, stream);
                }
            }
        }

        public static class ModPathResourcesSupplier implements Pack.ResourcesSupplier {
            private final IModFile modFile;
            private final Path content;

            public ModPathResourcesSupplier(IModFile modFile, Path content) {
                this.modFile = modFile;
                this.content = content;
            }

            @Override
            public PackResources openPrimary(PackLocationInfo location) {
                return new ModFilePackResources(location, this.modFile, this.content.toString());
            }

            @Override
            public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                PackResources resources = this.openPrimary(location);
                List<String> overlays = metadata.overlays();
                if (overlays.isEmpty()) {
                    return resources;
                } else {
                    return null;
                }
            }
        }
    }
}