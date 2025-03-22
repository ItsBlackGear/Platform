package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.GameRendering;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemModelShaper itemModelShaper;

    @Redirect(
        method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/BakedModel;usesBlockLight()Z"
        )
    )
    private boolean onRenderItem(BakedModel instance, ItemStack stack) {
        return !GameRendering.HAND_HELD_MODELS.containsKey(stack.getItem()) && instance.usesBlockLight();
    }

    @ModifyVariable(
        method = "render",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private BakedModel onRender(
        BakedModel model,
        ItemStack stack,
        ItemTransforms.TransformType transformType
    ) {
        boolean simple = transformType == ItemTransforms.TransformType.GUI ||
            transformType == ItemTransforms.TransformType.GROUND ||
            transformType == ItemTransforms.TransformType.FIXED;

        if (simple && GameRendering.HAND_HELD_MODELS.containsKey(stack.getItem())) {
            return this.itemModelShaper.getItemModel(stack);
        }

        return model;
    }

    @Redirect(
        method = "getModel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;"
        )
    )
    private BakedModel onGettingModel(ItemModelShaper shaper, ItemStack stack) {
        Item item = stack.getItem();
        if (GameRendering.HAND_HELD_MODELS.containsKey(item)) {
            return shaper.getModelManager().getModel(GameRendering.HAND_HELD_MODELS.get(item));
        } else {
            return shaper.getItemModel(stack);
        }
    }
}