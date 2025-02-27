package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.common.item.ItemTransformations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Random;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {
    @Shadow @Final private Random random;
    @Shadow @Final private ItemRenderer itemRenderer;
    @Shadow protected abstract int getRenderAmount(ItemStack stack);

    protected ItemEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Inject(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        Optional<BakedModel> model = Optional.ofNullable(ItemTransformations.modifyItemRendering(entity.getItem(), ItemTransforms.TransformType.GROUND));
        if (model.isPresent()) {
            ci.cancel();

            matrices.pushPose();
            ItemStack stack = entity.getItem();
            int itemDamage = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
            this.random.setSeed(itemDamage);
            BakedModel original = this.itemRenderer.getModel(stack, entity.level, null);
            boolean isGui3D = original.isGui3d();
            int renderAmount = this.getRenderAmount(stack);
            float bob = Mth.sin(((float) entity.getAge() + partialTicks) / 10.0F + entity.bobOffs) * 0.1F + 0.1F;
            float scale = original.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
            matrices.translate(0.0, bob + 0.25F * scale, 0.0);
            float spin = entity.getSpin(partialTicks);
            matrices.mulPose(Vector3f.YP.rotation(spin));
            float scaleX = original.getTransforms().ground.scale.x();
            float scaleY = original.getTransforms().ground.scale.y();
            float scaleZ = original.getTransforms().ground.scale.z();

            if (!isGui3D) {
                float x = -0.0F * (float)(renderAmount - 1) * 0.5F * scaleX;
                float y = -0.0F * (float)(renderAmount - 1) * 0.5F * scaleY;
                float z = -0.09375F * (float)(renderAmount - 1) * 0.5F * scaleZ;
                matrices.translate(x, y, z);
            }

            for (int i = 0; i < renderAmount; ++i) {
                matrices.pushPose();
                if (i > 0) {
                    if (isGui3D) {
                        float x = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float y = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float z = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        matrices.translate(x, y, z);
                    } else {
                        float x = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        float z = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        matrices.translate(x, z, 0.0);
                    }
                }

                this.itemRenderer.render(stack, ItemTransforms.TransformType.GROUND, false, matrices, buffer, packedLight, OverlayTexture.NO_OVERLAY, model.get());
                matrices.popPose();
                if (!isGui3D) {
                    matrices.translate(0.0F * scaleX, 0.0F * scaleY, 0.09375F * scaleZ);
                }
            }

            matrices.popPose();
            super.render(entity, entityYaw, partialTicks, matrices, buffer, packedLight);
        }
    }
}