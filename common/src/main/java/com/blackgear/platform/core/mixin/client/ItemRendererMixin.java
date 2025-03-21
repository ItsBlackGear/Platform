package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.GameRendering;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemModelShaper itemModelShaper;

    @WrapOperation(
        method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/BakedModel;usesBlockLight()Z"
        )
    )
    private boolean onRenderItem(BakedModel instance, Operation<Boolean> original, ItemStack stack) {
        return !GameRendering.SPECIAL_ITEMS.containsKey(stack.getItem()) && original.call(instance);
    }

    @Inject(
        method = "render",
        at = @At("HEAD")
    )
    private void onRender(
        ItemStack stack,
        ItemTransforms.TransformType transformType,
        boolean leftHand,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int combinedLight,
        int combinedOverlay,
        BakedModel model,
        CallbackInfo ci,
        @Local(argsOnly = true) LocalRef<BakedModel> modelRef
    ) {
        boolean simple = transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.GROUND || transformType == ItemTransforms.TransformType.FIXED;
        if (!simple) return;

        if (GameRendering.SPECIAL_ITEMS.containsKey(stack.getItem())) {
            modelRef.set(this.itemModelShaper.getItemModel(stack));
        }
    }

    @WrapOperation(
        method = "getModel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;"
        )
    )
    private BakedModel onGettingModel(
        ItemModelShaper shaper,
        ItemStack stack,
        Operation<BakedModel> original
    ) {
        Item item = stack.getItem();
        if (GameRendering.SPECIAL_ITEMS.containsKey(item)) {
            return shaper.getModelManager().getModel(GameRendering.SPECIAL_ITEMS.get(item));
        } else {
            return original.call(shaper, stack);
        }
    }
}