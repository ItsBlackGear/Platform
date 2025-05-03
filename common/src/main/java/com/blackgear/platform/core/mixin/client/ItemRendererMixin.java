package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.GameRendering;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemModelShaper itemModelShaper;

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    public BakedModel onModelLoading(
        BakedModel model,
        ItemStack stack,
        ItemDisplayContext context,
        boolean leftHanded,
        PoseStack matrices,
        MultiBufferSource source,
        int packedLight,
        int packedOverlay
    ) {
        boolean simple = context == ItemDisplayContext.GUI
            || context == ItemDisplayContext.GROUND
            || context == ItemDisplayContext.FIXED;

        if (GameRendering.HAND_HELD_MODELS.containsKey(stack.getItem()) && !simple) {
            ResourceLocation location = GameRendering.HAND_HELD_MODELS.get(stack.getItem());
            return this.itemModelShaper.getModelManager().getModel(ModelResourceLocation.inventory(location));
        }

        return model;
    }
}