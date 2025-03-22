package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.GameRendering;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
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

    @ModifyVariable(
        method = "render",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private BakedModel onRender(
        BakedModel model,
        ItemStack stack,
        ItemDisplayContext displayContext
    ) {
        boolean simple = displayContext == ItemDisplayContext.GUI ||
            displayContext == ItemDisplayContext.GROUND ||
            displayContext == ItemDisplayContext.FIXED;

        if (simple &&GameRendering.HAND_HELD_MODELS.containsKey(stack.getItem())) {
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