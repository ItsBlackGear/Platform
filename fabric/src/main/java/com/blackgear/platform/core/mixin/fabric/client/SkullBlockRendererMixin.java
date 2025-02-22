package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.RendererRegistry;
import com.blackgear.platform.client.event.SkullRegistry;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(SkullBlockRenderer.class)
public class SkullBlockRendererMixin {
    @Inject(
        method = "createSkullRenderers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/SkullModel;<init>(Lnet/minecraft/client/model/geom/ModelPart;)V",
            ordinal = 4
        )
    )
    private static void addSkullModels(EntityModelSet models, CallbackInfoReturnable<Map<SkullBlock.Type, SkullModelBase>> cir, @Local ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder) {
        RendererRegistry.MODEL_BY_SKULL.forEach((type, pair) -> builder.put(type, pair.getFirst().apply(models.bakeLayer(pair.getSecond()))));
        SkullRegistry.MODEL_BY_SKULL.forEach((type, pair) -> builder.put(type, pair.getFirst().apply(models.bakeLayer(pair.getSecond()))));
    }

    @Inject(
        method = "method_3580",
        at = @At("TAIL")
    )
    private static void addSkullTextures(HashMap<SkullBlock.Type, ResourceLocation> map, CallbackInfo ci) {
        map.putAll(RendererRegistry.TEXTURE_BY_SKULL);
        map.putAll(SkullRegistry.TEXTURE_BY_SKULL);
    }
}