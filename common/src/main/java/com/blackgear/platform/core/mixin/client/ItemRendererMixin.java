package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.common.item.ItemTransformations;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow public abstract void render(ItemStack itemStack, TransformType transformType, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model);
    @Shadow public static VertexConsumer getFoilBufferDirect(MultiBufferSource buffer, RenderType renderType, boolean noEntity, boolean withGlint) { throw new AssertionError(); }
    @Shadow public static VertexConsumer getFoilBuffer(MultiBufferSource buffer, RenderType renderType, boolean isItem, boolean glint) { throw new AssertionError(); }
    @Shadow protected abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack matrixStack, VertexConsumer buffer);
    @Shadow @Final private TextureManager textureManager;
    @Shadow public float blitOffset;

    @Inject(
        method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$renderGuiItem(
        ItemStack stack,
        int x,
        int y,
        BakedModel original,
        CallbackInfo ci
    ) {
        Optional<BakedModel> modified = Optional.ofNullable(ItemTransformations.modifyItemRendering(stack, TransformType.GUI));
        modified.ifPresent(model -> {
            ci.cancel();
            RenderSystem.pushMatrix();
            this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
            this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableAlphaTest();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.translatef((float)x, (float)y, 100.0F + this.blitOffset);
            RenderSystem.translatef(8.0F, 8.0F, 0.0F);
            RenderSystem.scalef(1.0F, -1.0F, 1.0F);
            RenderSystem.scalef(16.0F, 16.0F, 16.0F);
            PoseStack matrices = new PoseStack();
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            boolean flatLight = !model.usesBlockLight();
            if (flatLight) {
                Lighting.setupForFlatItems();
            }

            this.render(stack, TransformType.GUI, false, matrices, buffer, 15728880, OverlayTexture.NO_OVERLAY, model);
            buffer.endBatch();
            RenderSystem.enableDepthTest();
            if (flatLight) {
                Lighting.setupFor3DItems();
            }

            RenderSystem.disableAlphaTest();
            RenderSystem.disableRescaleNormal();
            RenderSystem.popMatrix();
        });
    }

    @Inject(
        method = "render",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$renderItem(
        ItemStack stack,
        TransformType transform,
        boolean leftHand,
        PoseStack matrices,
        MultiBufferSource buffer,
        int combinedLight,
        int combinedOverlay,
        BakedModel original,
        CallbackInfo ci
    ) {
        Optional<BakedModel> modified = Optional.ofNullable(ItemTransformations.modifyItemRendering(stack, transform));
        modified.ifPresent(model -> {
            ci.cancel();
            if (!stack.isEmpty()) {
                matrices.pushPose();
                boolean isSimpleTransform = transform == TransformType.GUI || transform == TransformType.GROUND || transform == TransformType.FIXED;

                model.getTransforms().getTransform(transform).apply(leftHand, matrices);
                matrices.translate(-0.5, -0.5, -0.5);

                if (model.isCustomRenderer() || !isSimpleTransform) {
                    BlockEntityWithoutLevelRenderer.instance.renderByItem(stack, transform, matrices, buffer, combinedLight, combinedOverlay);
                } else {
                    boolean isOpaque = true;
                    if (transform != TransformType.GUI && !transform.firstPerson() && stack.getItem() instanceof BlockItem) {
                        Block block = ((BlockItem) stack.getItem()).getBlock();
                        isOpaque = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
                    }

                    RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, isOpaque);
                    VertexConsumer vertices = getFoilBuffer(buffer, renderType, true, stack.hasFoil());
                    if (isOpaque) {
                        vertices = getFoilBufferDirect(buffer, renderType, true, stack.hasFoil());
                    }

                    this.renderModelLists(model, stack, combinedLight, combinedOverlay, matrices, vertices);
                }
            }

            matrices.popPose();
        });
    }
}