package com.blackgear.platform.core.mixin.client.emissive;

import com.blackgear.platform.client.v2.emissive.EmissiveModelReloader;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public class ModelManagerMixin {
    @Unique @Nullable private volatile EmissiveModelReloader reloader;

    @Inject(
        method = "reload",
        at = @At("HEAD")
    )
    private void platform$onReloadPre(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager manager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        this.reloader = new EmissiveModelReloader(manager);

        EmissiveModelReloader reloader = this.reloader;
        if (reloader != null) reloader.setContext();
    }

    @Inject(
        method = "reload",
        at = @At("RETURN")
    )
    private void platform$onReload(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        EmissiveModelReloader reloader = this.reloader;
        if (reloader != null) reloader.clearContext();
    }

    @ModifyReturnValue(
        method = "reload",
        at = @At("RETURN")
    )
    private CompletableFuture<Void> platform$onReloadPost(CompletableFuture<Void> original) {
        return original.thenRun(() -> this.reloader = null);
    }

    @Inject(
        method = "loadModels",
        at = @At("HEAD")
    )
    private void platform$onBakePre(ProfilerFiller profiler, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, ModelBakery bakery, CallbackInfoReturnable<?> cir) {
        EmissiveModelReloader reloader = this.reloader;
        if (reloader != null) reloader.beforeBaking(bakery);
    }
}