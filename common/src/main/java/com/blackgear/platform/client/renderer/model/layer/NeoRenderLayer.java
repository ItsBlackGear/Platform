package com.blackgear.platform.client.renderer.model.layer;

import com.blackgear.platform.client.renderer.NeoRenderLayerParent;
import com.blackgear.platform.client.renderer.model.NeoEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class NeoRenderLayer<T extends Entity, M extends NeoEntityModel<T>> {
    private final NeoRenderLayerParent<T, M> renderer;
    
    public NeoRenderLayer(NeoRenderLayerParent<T, M> renderLayerParent) {
        this.renderer = renderLayerParent;
    }
    
    protected static <T extends LivingEntity> void coloredCutoutModelCopyLayerRender(
        NeoEntityModel<T> entityModel,
        NeoEntityModel<T> model,
        ResourceLocation texture,
        PoseStack stack,
        MultiBufferSource buffer,
        int packedLight,
        T entity,
        float walkAnimationPos,
        float walkAnimationSpeed,
        float ageInTicks,
        float xRot,
        float yRot,
        float partialTicks,
        float red,
        float green,
        float blue
    ) {
        if (!entity.isInvisible()) {
            entityModel.copyPropertiesTo(model);
            model.prepareMobModel(entity, walkAnimationPos, walkAnimationSpeed, partialTicks);
            model.setupAnim(entity, walkAnimationPos, walkAnimationSpeed, ageInTicks, xRot, yRot);
            renderColoredCutoutModel(model, texture, stack, buffer, packedLight, entity, red, green, blue);
        }
    }
    
    protected static <T extends LivingEntity> void renderColoredCutoutModel(
        NeoEntityModel<T> model,
        ResourceLocation texture,
        PoseStack stack,
        MultiBufferSource buffer,
        int packedLight,
        T entity,
        float red,
        float green,
        float blue
    ) {
        VertexConsumer vertex = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(stack, vertex, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), red, green, blue, 1.0F);
    }
    
    public M getParentModel() {
        return this.renderer.getModel();
    }
    
    protected ResourceLocation getTextureLocation(T entity) {
        return this.renderer.getTextureLocation(entity);
    }
    
    public abstract void render(
        PoseStack stack,
        MultiBufferSource buffer,
        int packedLight,
        T entity,
        float walkAnimationPos,
        float walkAnimationSpeed,
        float partialTicks,
        float ageInTicks,
        float xRot,
        float yRot
    );
}