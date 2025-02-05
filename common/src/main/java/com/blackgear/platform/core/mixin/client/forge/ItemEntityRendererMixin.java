package com.blackgear.platform.core.mixin.client.forge;

import com.blackgear.platform.common.item.ItemTransformations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {
    @Shadow @Final private ItemRenderer itemRenderer;

    @Shadow @Final private RandomSource random;

    @Shadow protected abstract int getRenderAmount(ItemStack stack);

    protected ItemEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

//    @ModifyArgs(
//        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
//        )
//    )
//    private void vb$renderItemOnGround(Args args) {
//        Optional.ofNullable(ItemTransformations.modifyItemRendering(args.get(0), args.get(1))).ifPresent(model -> args.set(7, model));
//    }

    @Inject(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        Optional<BakedModel> model = Optional.ofNullable(ItemTransformations.modifyItemRendering(entity.getItem(), ItemTransforms.TransformType.GROUND));
        if (model.isPresent()) {
            ci.cancel();
            matrixStack.pushPose();
            ItemStack itemStack = entity.getItem();
            int i = itemStack.isEmpty() ? 187 : Item.getId(itemStack.getItem()) + itemStack.getDamageValue();
            this.random.setSeed(i);
            BakedModel bakedModel = this.itemRenderer.getModel(itemStack, entity.level, null, entity.getId());
            boolean bl = bakedModel.isGui3d();
            int j = this.getRenderAmount(itemStack);
            float f = 0.25F;
            float g = Mth.sin(((float)entity.getAge() + partialTicks) / 10.0F + entity.bobOffs) * 0.1F + 0.1F;
            float h = bakedModel.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
            matrixStack.translate(0.0, g + 0.25F * h, 0.0);
            float k = entity.getSpin(partialTicks);
            matrixStack.mulPose(Vector3f.YP.rotation(k));
            float l = bakedModel.getTransforms().ground.scale.x();
            float m = bakedModel.getTransforms().ground.scale.y();
            float n = bakedModel.getTransforms().ground.scale.z();
            float p;
            float q;
            if (!bl) {
                float o = -0.0F * (float)(j - 1) * 0.5F * l;
                p = -0.0F * (float)(j - 1) * 0.5F * m;
                q = -0.09375F * (float)(j - 1) * 0.5F * n;
                matrixStack.translate(o, p, q);
            }

            for(int r = 0; r < j; ++r) {
                matrixStack.pushPose();
                if (r > 0) {
                    if (bl) {
                        p = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        q = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        matrixStack.translate(p, q, s);
                    } else {
                        p = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        q = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        matrixStack.translate(p, q, 0.0);
                    }
                }

                this.itemRenderer.render(itemStack, ItemTransforms.TransformType.GROUND, false, matrixStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, model.get());
                matrixStack.popPose();
                if (!bl) {
                    matrixStack.translate(0.0F * l, 0.0F * m, 0.09375F * n);
                }
            }

            matrixStack.popPose();
            super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }
    }
}