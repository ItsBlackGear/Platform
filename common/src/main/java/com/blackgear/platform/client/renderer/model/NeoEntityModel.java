package com.blackgear.platform.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class NeoEntityModel<T extends Entity> extends Model {
    public float attackTime;
    public boolean riding;
    public boolean young = true;
    
    protected NeoEntityModel() {
        this(RenderType::entityCutoutNoCull);
    }
    
    protected NeoEntityModel(Function<ResourceLocation, RenderType> factory) {
        super(factory);
    }
    
    public abstract void setupAnim(T entity, float walkAnimationPos, float walkAnimationSpeed, float ageInTicks, float netHeadYaw, float headPitch);
    
    public void prepareMobModel(T entity, float walkAnimationPos, float walkAnimationSpeed, float partialTicks) {
    }
    
    public void copyPropertiesTo(NeoEntityModel<T> model) {
        model.attackTime = this.attackTime;
        model.riding = this.riding;
        model.young = this.young;
    }
    
    @Override @Deprecated
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.renderToBuffer(stack, buffer, packedLight, packedOverlay, FastColor.ARGB32.color((int) alpha, (int) red, (int) green, (int) blue));
    }
    
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
    }
}