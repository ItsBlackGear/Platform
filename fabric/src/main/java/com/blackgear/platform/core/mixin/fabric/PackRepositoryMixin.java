package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.core.events.fabric.ResourcePackManagerImpl;
import com.blackgear.platform.core.mixin.access.PackRepositoryAccessor;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(RepositorySource[] original, CallbackInfo ci) {
        // Get current sources and add our additional packs
        PackRepositoryAccessor repository = (PackRepositoryAccessor) this;
        Set<RepositorySource> sources = new HashSet<>(repository.getSources());

        // Add server data packs
        ResourcePackManagerImpl.getAdditionalPacks(PackType.SERVER_DATA)
            .forEach(pack -> sources.add(onLoad -> onLoad.accept(pack.get())));
        repository.setSources(sources);
    }
}