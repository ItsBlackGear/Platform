package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.core.events.fabric.ResourcePackManagerImpl;
import com.blackgear.platform.core.mixin.access.PackRepositoryAccessor;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @ModifyExpressionValue(
        method = "openFresh",
        at = @At(
            value = "NEW",
            target = "([Lnet/minecraft/server/packs/repository/RepositorySource;)Lnet/minecraft/server/packs/repository/PackRepository;"
        )
    )
    private static PackRepository platform$addBuiltinDataPacks(PackRepository original) {
        if (original instanceof PackRepositoryAccessor repository) {
            HashSet<RepositorySource> sources = new HashSet<>(repository.getSources());
            sources.addAll(ResourcePackManagerImpl.PACKS);
            repository.setSources(sources);
        }

        return original;
    }
}