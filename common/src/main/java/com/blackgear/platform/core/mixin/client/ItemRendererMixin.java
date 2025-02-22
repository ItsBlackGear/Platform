package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.common.item.ItemTransformations;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.tags.ItemTags;
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
    @Shadow @Final private TextureManager textureManager;
    @Shadow public float blitOffset;
    @Shadow @Final private BlockEntityWithoutLevelRenderer blockEntityRenderer;

    @Shadow public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource buffer, RenderType renderType, PoseStack.Pose matrixEntry) { return null; }
    @Shadow public static VertexConsumer getCompassFoilBuffer(MultiBufferSource buffer, RenderType renderType, PoseStack.Pose matrixEntry) { return null; }
    @Shadow public static VertexConsumer getFoilBufferDirect(MultiBufferSource buffer, RenderType renderType, boolean noEntity, boolean withGlint) { return null; }
    @Shadow public static VertexConsumer getFoilBuffer(MultiBufferSource buffer, RenderType renderType, boolean isItem, boolean glint) { return null; }
    @Shadow protected abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack matrixStack, VertexConsumer buffer);
    @Shadow public abstract void render(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model);

    @Inject(
        method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$renderGuiItem(ItemStack stack, int x, int y, BakedModel original, CallbackInfo ci) {
        Optional<BakedModel> modified = Optional.ofNullable(ItemTransformations.modifyItemRendering(stack, ItemTransforms.TransformType.GUI));
        modified.ifPresent(model -> {
            ci.cancel();
            this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            PoseStack viewMatrix = RenderSystem.getModelViewStack();
            viewMatrix.pushPose();
            viewMatrix.translate(x, y, 100.0F + this.blitOffset);
            viewMatrix.translate(8.0, 8.0, 0.0);
            viewMatrix.scale(1.0F, -1.0F, 1.0F);
            viewMatrix.scale(16.0F, 16.0F, 16.0F);
            RenderSystem.applyModelViewMatrix();
            PoseStack matrices = new PoseStack();
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            boolean flatLight = !model.usesBlockLight();
            if (flatLight) {
                Lighting.setupForFlatItems();
            }

            this.render(stack, ItemTransforms.TransformType.GUI, false, matrices, buffer, 15728880, OverlayTexture.NO_OVERLAY, model);
            buffer.endBatch();
            RenderSystem.enableDepthTest();
            if (flatLight) {
                Lighting.setupFor3DItems();
            }

            viewMatrix.popPose();
            RenderSystem.applyModelViewMatrix();
        });
    }

    @Inject(
        method = "render",
        at = @At("HEAD"),
        cancellable = true
    )
    public void platform$render(
        ItemStack stack,
        ItemTransforms.TransformType transforms,
        boolean leftHand,
        PoseStack matrices,
        MultiBufferSource buffer,
        int combinedLight,
        int combinedOverlay,
        BakedModel original,
        CallbackInfo ci
    ) {
        Optional<BakedModel> modified = Optional.ofNullable(ItemTransformations.modifyItemRendering(stack, transforms));
        modified.ifPresent(model -> {
            ci.cancel();
            if (!stack.isEmpty()) {
                matrices.pushPose();
                boolean simpleTransform = transforms == ItemTransforms.TransformType.GUI || transforms == ItemTransforms.TransformType.GROUND || transforms == ItemTransforms.TransformType.FIXED;

                model.getTransforms().getTransform(transforms).apply(leftHand, matrices);
                matrices.translate(-0.5, -0.5, -0.5);

                if (model.isCustomRenderer() || !simpleTransform) {
                    this.blockEntityRenderer.renderByItem(stack, transforms, matrices, buffer, combinedLight, combinedOverlay);
                } else {
                    boolean isOpaque = true;
                    if (transforms != ItemTransforms.TransformType.GUI && !transforms.firstPerson() && stack.getItem() instanceof BlockItem) {
                        Block block = ((BlockItem) stack.getItem()).getBlock();
                        isOpaque = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
                    }

                    RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, isOpaque);
                    VertexConsumer vertices = getFoilBuffer(buffer, renderType, true, stack.hasFoil());
                    if (stack.is(ItemTags.COMPASSES) && stack.hasFoil()) {
                        matrices.pushPose();
                        PoseStack.Pose pose = matrices.last();
                        if (transforms == ItemTransforms.TransformType.GUI) {
                            pose.pose().multiply(0.5F);
                        } else if (transforms.firstPerson()) {
                            pose.pose().multiply(0.75F);
                        }

                        if (isOpaque) {
                            vertices = getCompassFoilBufferDirect(buffer, renderType, pose);
                        } else {
                            vertices = getCompassFoilBuffer(buffer, renderType, pose);
                        }

                        matrices.popPose();
                    } else if (isOpaque) {
                        vertices = getFoilBufferDirect(buffer, renderType, true, stack.hasFoil());
                    }

                    this.renderModelLists(model, stack, combinedLight, combinedOverlay, matrices, vertices);
                }

                matrices.popPose();
            }
        });
    }
}