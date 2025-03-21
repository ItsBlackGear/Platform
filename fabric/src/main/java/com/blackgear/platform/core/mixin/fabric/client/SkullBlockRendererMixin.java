package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.fabric.GameRenderingImpl;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
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
import java.util.function.Function;

@Mixin(SkullBlockRenderer.class)
public class SkullBlockRendererMixin {
    @Inject(
        method = "createSkullRenderers",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;",
            ordinal = 5
        )
    )
    private static void addSkullModels(EntityModelSet models, CallbackInfoReturnable<Map<SkullBlock.Type, SkullModelBase>> cir, @Local ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder) {
        for (Map.Entry<SkullBlock.Type, Pair<Function<ModelPart, SkullModelBase>, ModelLayerLocation>> entry : GameRenderingImpl.MODEL_BY_SKULL.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().getFirst().apply(models.bakeLayer(entry.getValue().getSecond())));
        }
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private static void getRenderType(SkullBlock.Type skullType, GameProfile gameProfile, CallbackInfoReturnable<RenderType> cir) {
        for (Map.Entry<SkullBlock.Type, ResourceLocation> entry : GameRenderingImpl.TEXTURE_BY_SKULL.entrySet()) {
            if (entry.getKey() == skullType) {
                cir.setReturnValue(RenderType.entityCutoutNoCullZOffset(entry.getValue()));
            }
        }
    }
}