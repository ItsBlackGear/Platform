package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.v2.render.ItemRendererRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemModelShaper itemModelShaper;
    @Unique private Item capturedItem;
    @Unique private Item capturedHelditem;

    @Inject(method = "render", at = @At("HEAD"))
    private void platform$getItem(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        this.capturedItem = stack.getItem();
    }

    @ModifyVariable(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z",
            ordinal = 0,
            shift = At.Shift.BEFORE
        ),
        ordinal = 0,
        argsOnly = true
    )
    public BakedModel platform$render(BakedModel original) {
        if (ItemRendererRegistry.getHandModel(this.capturedItem) != null) {
            return this.itemModelShaper.getItemModel(this.capturedItem);
        }

        return original;
    }

    @Inject(method = "getModel", at = @At("HEAD"))
    public void platform$getModel(ItemStack stack, Level level, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        this.capturedHelditem = stack.getItem();
    }

    @ModifyVariable(
        method = "getModel",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;",
            shift = At.Shift.AFTER
        ),
        ordinal = 0
    )
    public BakedModel platform$getModel(BakedModel original) {
        ModelResourceLocation model = ItemRendererRegistry.getHandModel(this.capturedHelditem);
        if (model != null) {
            return this.itemModelShaper.getModelManager().getModel(model);
        }

        return original;
    }
}