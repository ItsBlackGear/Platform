package com.blackgear.platform.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class NeoAgeableHierarchicalModel<E extends Entity> extends NeoHierarchicalModel<E> {
    private final float youngScaleFactor;
    private final float bodyYOffset;
    
    public NeoAgeableHierarchicalModel(float youngScaleFactor, float bodyYOffset) {
        this(youngScaleFactor, bodyYOffset, RenderType::entityCutoutNoCull);
    }
    
    public NeoAgeableHierarchicalModel(float youngScaleFactor, float bodyYOffset, Function<ResourceLocation, RenderType> factory) {
        super(factory);
        this.youngScaleFactor = youngScaleFactor;
        this.bodyYOffset = bodyYOffset;
    }
    
    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        if (this.young) {
            stack.pushPose();
            stack.scale(this.youngScaleFactor, this.youngScaleFactor, this.youngScaleFactor);
            stack.translate(0.0F, this.bodyYOffset / 16.0F, 0.0F);
            this.root().render(stack, buffer, packedLight, packedOverlay, color);
            stack.popPose();
        } else {
            this.root().render(stack, buffer, packedLight, packedOverlay, color);
        }
    }
}