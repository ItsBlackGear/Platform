package com.blackgear.platform.core.mixin.forge.client;

import com.blackgear.platform.client.v2.render.BuiltinItemRendererRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class BEWLRMixin {
    @Inject(
        method = "renderByItem",
        at = @At("HEAD"),
        cancellable = true
    )
    private void renderDynamicItems(
        ItemStack stack,
        ItemDisplayContext displayContext,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        int packedOverlay,
        CallbackInfo ci
    ) {
        BuiltinItemRendererRegistry.Renderer renderer = BuiltinItemRendererRegistry.getInstance().get(stack.getItem());

        if (renderer != null) {
            renderer.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
            ci.cancel();
        }
    }
}