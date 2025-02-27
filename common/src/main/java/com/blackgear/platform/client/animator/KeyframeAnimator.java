package com.blackgear.platform.client.animator;

import com.blackgear.platform.client.renderer.model.NeoHierarchicalModel;
import com.blackgear.platform.client.renderer.model.animation.AnimationChannel;
import com.blackgear.platform.client.renderer.model.animation.AnimationDefinition;
import com.blackgear.platform.client.renderer.model.animation.Keyframe;
import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import com.blackgear.platform.common.entity.resource.AnimationState;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class KeyframeAnimator {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public static void animate(NeoHierarchicalModel<?> model, AnimationState state, AnimationDefinition definition, float time) {
        animate(model, state, definition, time, 1.0F);
    }

    public static void animateWalk(NeoHierarchicalModel<?> model, AnimationDefinition definition, float limbSwing, float limbSwingAmount, float maxSpeed, float scaleFactor) {
        long accumulatedTime = (long)(limbSwing * 50.0F * maxSpeed);
        float scale = Math.min(limbSwingAmount * scaleFactor, 1.0F);
        animate(model, definition, accumulatedTime, scale);
    }

    public static void animate(NeoHierarchicalModel<?> model, AnimationState state, AnimationDefinition definition, float time, float speed) {
        state.updateTime(time, speed);
        state.ifStarted((animationState) -> {
            animate(model, definition, animationState.getAccumulatedTime(), 1.0F);
        });
    }

    public static void applyStatic(NeoHierarchicalModel<?> model, AnimationDefinition definition) {
        animate(model, definition, 0L, 1.0F);
    }

    private static void animate(NeoHierarchicalModel<?> model, AnimationDefinition definition, long accumulatedTime, float scale) {
        float elapsed = getElapsedSeconds(definition, accumulatedTime);

        for (Map.Entry<String, List<AnimationChannel>> animations : definition.boneAnimations().entrySet()) {
            Optional<NeoModelPart> optional = model.getAnyDescendantWithName(animations.getKey());
            List<AnimationChannel> channels = animations.getValue();
            optional.ifPresent(part -> channels.forEach(channel -> {
                Keyframe[] frames = channel.keyframes();
                int frameIndex = Math.max(0, Mth.binarySearch(0, frames.length, (i) -> elapsed <= frames[i].timestamp()) - 1);
                int nextFrameIndex = Math.min(frames.length - 1, frameIndex + 1);
                Keyframe frame = frames[frameIndex];
                Keyframe nextFrame = frames[nextFrameIndex];
                float remaining = elapsed - frame.timestamp();
                float delta = nextFrameIndex != frameIndex ? Mth.clamp(remaining / (nextFrame.timestamp() - frame.timestamp()), 0.0F, 1.0F) : 0.0F;
                nextFrame.interpolation().apply(ANIMATION_VECTOR_CACHE, delta, frames, frameIndex, nextFrameIndex, scale);
                channel.target().apply(part, ANIMATION_VECTOR_CACHE);
            }));
        }
    }

    private static float getElapsedSeconds(AnimationDefinition definition, long accumulatedTime) {
        float seconds = (float)accumulatedTime / 1000.0F;
        return definition.looping() ? seconds % definition.lengthInSeconds() : seconds;
    }
}