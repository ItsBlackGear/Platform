package com.blackgear.platform.core.events.forge;

import com.blackgear.platform.core.events.ResourcePackManager;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ResourcePackManagerImpl {
    public static void registerPack(Consumer<ResourcePackManager.Event> listener) {
        Consumer<AddPackFindersEvent> consumer = event -> listener.accept((packType, pack) -> {
            if (pack == null) return;

            if (event.getPackType() == packType && pack.get() != null) {
                event.addRepositorySource(onLoad -> onLoad.accept(pack.get()));
            }
        });
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }
}