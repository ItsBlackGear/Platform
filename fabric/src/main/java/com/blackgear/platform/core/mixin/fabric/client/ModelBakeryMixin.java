package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.RendererRegistry;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow
    protected abstract void loadTopLevel(ModelResourceLocation location);

    @Inject(
        method = "<init>",
        at = @At(
            value = "CONSTANT",
            args = "stringValue=special"
        )
    )
    private void platform$registerSpecialModels(ResourceManager resourceManager, BlockColors blockColors, ProfilerFiller profiler, int maxMipmapLevel, CallbackInfo ci) {
        RendererRegistry.SPECIAL_MODELS.forEach(this::loadTopLevel);
    }
}