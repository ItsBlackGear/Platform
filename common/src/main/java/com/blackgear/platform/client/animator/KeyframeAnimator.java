package com.blackgear.platform.client.animator;

import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Backport the updated animation system from 1.20+
 */
@Environment(EnvType.CLIENT)
public class KeyframeAnimator {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
    
    public static void animate(HierarchicalModel<?> model, AnimationState state, AnimationDefinition definition, float time) {
        animate(model, state, definition, time, 1F);
    }
    
    public static void animateWalk(HierarchicalModel<?> model, AnimationDefinition definition, float limbSwing, float limbSwingAmount, float maxSpeed, float scaleFactor) {
        long accumulatedTime = (long)(limbSwing * 50F * maxSpeed);
        float scale = Math.min(limbSwingAmount * scaleFactor, 1F);
        animate(model, definition, accumulatedTime, scale);
    }
    
    public static void animate(HierarchicalModel<?> model, AnimationState state, AnimationDefinition definition, float time, float speed) {
        state.updateTime(time, speed);
        state.ifStarted(animationState -> animate(model, definition, animationState.getAccumulatedTime(), 1F));
    }
    
    public static void applyStatic(HierarchicalModel<?> model, AnimationDefinition definition) {
        animate(model, definition, 0L, 1F);
    }
    
    private static void animate(HierarchicalModel<?> model, AnimationDefinition definition, long accumulatedTime, float scale) {
        float elapsed = getElapsedSeconds(definition, accumulatedTime);
        
        for (Map.Entry<String, List<AnimationChannel>> animation : definition.boneAnimations().entrySet()) {
            Optional<ModelPart> optional = model.getAnyDescendantWithName(animation.getKey());
            List<AnimationChannel> channel = animation.getValue();
            optional.ifPresent(modelPart -> channel.forEach(animationChannel -> {
                Keyframe[] frames = animationChannel.keyframes();
                int frameIndex = Math.max(0, Mth.binarySearch(0, frames.length, i -> elapsed <= frames[i].timestamp()) - 1);
                int nextFrameIndex = Math.min(frames.length - 1, frameIndex + 1);
                Keyframe frame = frames[frameIndex];
                Keyframe nextFrame = frames[nextFrameIndex];
                float remaining = elapsed - frame.timestamp();
                float delta = nextFrameIndex != frameIndex ? Mth.clamp(remaining / (nextFrame.timestamp() - frame.timestamp()), 0.0f, 1.0f) : 0.0f;
                nextFrame.interpolation().apply(KeyframeAnimator.ANIMATION_VECTOR_CACHE, delta, frames, frameIndex, nextFrameIndex, scale);
                animationChannel.target().apply(modelPart, KeyframeAnimator.ANIMATION_VECTOR_CACHE);
            }));
        }
    }
    
    private static float getElapsedSeconds(AnimationDefinition definition, long accumulatedTime) {
        float seconds = (float)accumulatedTime / 1000F;
        return definition.looping() ? seconds % definition.lengthInSeconds() : seconds;
    }
}