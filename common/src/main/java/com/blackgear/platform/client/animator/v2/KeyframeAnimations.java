package com.blackgear.platform.client.animator.v2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class KeyframeAnimations {
    public static void animate(HierarchicalModel<?> model, AnimationDefinition animationDefinition, float ageInTicks, long accumulatedTime, float scale, Vector3f animationVecCache) {
        float elapsedSeconds = getElapsedSeconds(animationDefinition, accumulatedTime);
        AnimationChannel.Query query = () -> ageInTicks * 0.001F;

        for (Map.Entry<String, List<AnimationChannel>> animation : animationDefinition.boneAnimations().entrySet()) {
            Optional<ModelPart> entry = model.getAnyDescendantWithName(animation.getKey());
            List<AnimationChannel> channels = animation.getValue();

            entry.ifPresent(modelPart -> channels.forEach(channel -> {
                Keyframe[] keyframes = channel.keyframes();
                int currentKeyframeIndex = Math.max(0, Mth.binarySearch(0, keyframes.length, ix -> elapsedSeconds <= keyframes[ix].timestamp()) - 1);
                int nextKeyframeIndex = Math.min(keyframes.length - 1, currentKeyframeIndex + 1);
                Keyframe currentKeyframe = keyframes[currentKeyframeIndex];
                Keyframe nextKeyframe = keyframes[nextKeyframeIndex];
                float timeSinceCurrentKeyframe = elapsedSeconds - currentKeyframe.timestamp();
                float interpolationFactor = 0.0F;

                if (nextKeyframeIndex != currentKeyframeIndex) {
                    interpolationFactor = Mth.clamp(timeSinceCurrentKeyframe / (nextKeyframe.timestamp() - currentKeyframe.timestamp()), 0.0F, 1.0F);
                }

                nextKeyframe.interpolation().apply(animationVecCache, query, interpolationFactor, keyframes, currentKeyframeIndex, nextKeyframeIndex, scale);
                channel.target().apply(modelPart, animationVecCache);
            }));
        }
    }

    private static float getElapsedSeconds(AnimationDefinition animationDefinition, long accumulatedTime) {
        float timeInSeconds = (float) accumulatedTime / 1000.0F;
        return animationDefinition.looping() ? timeInSeconds % animationDefinition.lengthInSeconds() : timeInSeconds;
    }

    public static Vector3f posVec(float x, float y, float z) {
        return new Vector3f(x, -y, z);
    }

    public static Vector3f degreeVec(float xDegrees, float yDegrees, float zDegrees) {
        return new Vector3f(xDegrees * Mth.DEG_TO_RAD, yDegrees * Mth.DEG_TO_RAD, zDegrees * Mth.DEG_TO_RAD);
    }

    public static Vector3f scaleVec(double xScale, double yScale, double zScale) {
        return new Vector3f((float)(xScale - 1.0), (float)(yScale - 1.0), (float)(zScale - 1.0));
    }
}