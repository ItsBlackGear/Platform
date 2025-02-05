package com.blackgear.platform.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class AgeableHierarchicalModel<E extends Entity> extends HierarchicalModel<E> {
    private final float youngScaleFactor;
    private final float bodyYOffset;

    public AgeableHierarchicalModel(float youngScaleFactor, float bodyYOffset) {
        this(youngScaleFactor, bodyYOffset, RenderType::entityCutoutNoCull);
    }

    public AgeableHierarchicalModel(float youngScaleFactor, float bodyYOffset, Function<ResourceLocation, RenderType> factory) {
        super(factory);
        this.youngScaleFactor = youngScaleFactor;
        this.bodyYOffset = bodyYOffset;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.young) {
            matrices.pushPose();
            matrices.scale(this.youngScaleFactor, this.youngScaleFactor, this.youngScaleFactor);
            matrices.translate(0.0F, this.bodyYOffset / 16.0F, 0.0F);
            this.root().render(matrices, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            matrices.popPose();
        } else {
            this.root().render(matrices, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}