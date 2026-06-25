package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.core.events.fabric.ResourcePackManagerImpl;
import com.blackgear.platform.core.mixin.access.PackRepositoryAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;

@Mixin(ServerPacksSource.class)
public class ServerPacksSourceMixin {
    @ModifyReturnValue(
        method = "createPackRepository(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Lnet/minecraft/server/packs/repository/PackRepository;",
        at = @At("RETURN")
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