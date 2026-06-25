package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.v2.render.DynamicItemRenderer;
import com.blackgear.platform.client.v2.render.ItemRendererRegistry;
import com.blackgear.platform.core.util.event.ResultHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemModelShaper itemModelShaper;

    @Shadow @Final private ItemColors itemColors;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(
        ItemStack stack,
        ItemDisplayContext context,
        boolean leftHand,
        PoseStack pose,
        MultiBufferSource buffer,
        int light,
        int overlay,
        BakedModel model,
        CallbackInfo ci
    ) {
        DynamicItemRenderer.Renderer renderer = DynamicItemRenderer.INSTANCE.get().get(stack.getItem());
        if (renderer != null && renderer.shouldUse()) {
            pose.pushPose();
            renderer.renderFirstPerson(stack, context, leftHand, pose, buffer, light, overlay, model, this.itemModelShaper, this.itemColors);
            pose.popPose();
            ci.cancel();
        }
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private BakedModel render(BakedModel original, ItemStack stack, ItemDisplayContext context) {
        ItemRendererRegistry.Renderer renderer = ItemRendererRegistry.INSTANCE.get().get(stack.getItem());
        if (renderer != null && renderer.shouldUse()) {
            ResultHolder<BakedModel> result = renderer.renderFirstPerson(stack, context, this.itemModelShaper);
            if (result.isCancelled()) return result.getValue();
        }

        return original;
    }

    @ModifyVariable(method = "getModel", at = @At(value = "STORE"))
    private BakedModel getModel(BakedModel original, ItemStack stack) {
        ItemRendererRegistry.Renderer renderer = ItemRendererRegistry.INSTANCE.get().get(stack.getItem());
        if (renderer != null && renderer.shouldUse()) {
            ResultHolder<BakedModel> result = renderer.renderThirdPerson(stack, this.itemModelShaper);
            if (result.isCancelled()) return result.getValue();
        }

        DynamicItemRenderer.Renderer dynamic = DynamicItemRenderer.INSTANCE.get().get(stack.getItem());
        if (dynamic != null && dynamic.shouldUse()) {
            ResultHolder<BakedModel> result = dynamic.renderThirdPerson(stack, this.itemModelShaper);
            if (result.isCancelled()) return result.getValue();
        }

        return original;
    }
}