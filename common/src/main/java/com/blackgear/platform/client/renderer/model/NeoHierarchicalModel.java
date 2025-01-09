package com.blackgear.platform.client.renderer.model;

import com.blackgear.platform.client.renderer.model.animation.AnimationDefinition;
import com.blackgear.platform.common.entity.resource.AnimationState;
import com.blackgear.platform.client.renderer.model.animation.KeyframeAnimations;
import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class NeoHierarchicalModel<E extends Entity> extends NeoEntityModel<E> {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
    
    public NeoHierarchicalModel() {
        this(RenderType::entityCutoutNoCull);
    }
    
    public NeoHierarchicalModel(Function<ResourceLocation, RenderType> factory) {
        super(factory);
    }
    
    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root().render(stack, buffer, packedLight, packedOverlay, color);
    }
    
    public abstract NeoModelPart root();
    
    public Optional<NeoModelPart> getAnyDescendantWithName(String childName) {
        return childName.equals("root")
            ? Optional.of(this.root())
            : this.root()
                .getAllParts()
                .filter(part -> part.hasChild(childName))
                .findFirst()
                .map(part -> part.getChild(childName));
    }
    
    protected void animate(AnimationState state, AnimationDefinition definition, float ageInTicks) {
        this.animate(state, definition, ageInTicks, 1.0F);
    }

    protected void animateWalk(AnimationDefinition definition, float walkAnimationPos, float walkAnimationSpeed, float speedFactor, float scaleFactor) {
        long ageInTicks = (long) (walkAnimationPos * 50.0F * speedFactor);
        float speedMultiplier = Math.min(walkAnimationSpeed * scaleFactor, 1.0F);
        KeyframeAnimations.animate(this, definition, ageInTicks, speedMultiplier, ANIMATION_VECTOR_CACHE);
    }

    protected void animate(AnimationState state, AnimationDefinition definition, float ageInTicks, float speedMultiplier) {
        state.updateTime(ageInTicks, speedMultiplier);
        state.ifStarted(stateX -> KeyframeAnimations.animate(this, definition, stateX.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE));
    }

    protected void applyStatic(AnimationDefinition definition) {
        KeyframeAnimations.animate(this, definition, 0L, 1.0F, ANIMATION_VECTOR_CACHE);
    }
}