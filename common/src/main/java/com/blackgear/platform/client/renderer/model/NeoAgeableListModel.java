package com.blackgear.platform.client.renderer.model;

import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class NeoAgeableListModel<E extends Entity> extends NeoEntityModel<E> {
    private final boolean scaleHead;
    private final float babyYHeadOffset;
    private final float babyZHeadOffset;
    private final float babyHeadScale;
    private final float babyBodyScale;
    private final float bodyYOffset;
    
    protected NeoAgeableListModel(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset) {
        this(scaleHead, babyYHeadOffset, babyZHeadOffset, 2.0F, 2.0F, 24.0F);
    }
    
    protected NeoAgeableListModel(
        boolean scaleHead,
        float babyYHeadOffset,
        float babyZHeadOffset,
        float babyHeadScale,
        float babyBodyScale,
        float bodyYOffset
    ) {
        this(RenderType::entityCutoutNoCull, scaleHead, babyYHeadOffset, babyZHeadOffset, babyHeadScale, babyBodyScale, bodyYOffset);
    }
    
    protected NeoAgeableListModel(
        Function<ResourceLocation, RenderType> factory,
        boolean scaleHead,
        float babyYHeadOffset,
        float babyZHeadOffset,
        float babyHeadScale,
        float babyBodyScale,
        float bodyYOffset
    ) {
        super(factory);
        this.scaleHead = scaleHead;
        this.babyYHeadOffset = babyYHeadOffset;
        this.babyZHeadOffset = babyZHeadOffset;
        this.babyHeadScale = babyHeadScale;
        this.babyBodyScale = babyBodyScale;
        this.bodyYOffset = bodyYOffset;
    }
    
    protected NeoAgeableListModel() {
        this(false, 5.0F, 2.0F);
    }
    
    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        if (this.young) {
            stack.pushPose();
            if (this.scaleHead) {
                float headScale = 1.5F / this.babyHeadScale;
                stack.scale(headScale, headScale, headScale);
            }
            
            stack.translate(0.0F, this.babyYHeadOffset / 16.0F, this.babyZHeadOffset / 16.0F);
            this.headParts().forEach(modelPart -> modelPart.render(stack, buffer, packedLight, packedOverlay, color));
            stack.popPose();
            stack.pushPose();
            float bodyScale = 1.0F / this.babyBodyScale;
            stack.scale(bodyScale, bodyScale, bodyScale);
            stack.translate(0.0F, this.bodyYOffset / 16.0F, 0.0F);
            this.bodyParts().forEach(modelPart -> modelPart.render(stack, buffer, packedLight, packedOverlay, color));
            stack.popPose();
        } else {
            this.headParts().forEach(modelPart -> modelPart.render(stack, buffer, packedLight, packedOverlay, color));
            this.bodyParts().forEach(modelPart -> modelPart.render(stack, buffer, packedLight, packedOverlay, color));
        }
    }
    
    protected abstract Iterable<NeoModelPart> headParts();
    
    protected abstract Iterable<NeoModelPart> bodyParts();
}
