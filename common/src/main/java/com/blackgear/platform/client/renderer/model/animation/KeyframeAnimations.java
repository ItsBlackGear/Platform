package com.blackgear.platform.client.renderer.model.animation;

import com.blackgear.platform.client.renderer.model.NeoHierarchicalModel;
import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class KeyframeAnimations {
    public static void animate(NeoHierarchicalModel<?> model, AnimationDefinition definition, long progress, float speed, Vector3f cache) {
        float elapsedSeconds = getElapsedSeconds(definition, progress);
        
        for (Map.Entry<String, List<AnimationChannel>> animations : definition.boneAnimations().entrySet()) {
            Optional<NeoModelPart> descendant = model.getAnyDescendantWithName(animations.getKey());
            List<AnimationChannel> channels = animations.getValue();
            descendant.ifPresent(part -> {
                channels.forEach(channel -> {
                    Keyframe[] keyframes = channel.keyframes();
                    int start = Math.max(0, Mth.binarySearch(0, keyframes.length, i -> elapsedSeconds <= keyframes[i].timestamp()) - 1);
                    int end = Math.min(keyframes.length - 1, start + 1);
                    Keyframe frameStart = keyframes[start];
                    Keyframe frameEnd = keyframes[end];
                    float current = elapsedSeconds - frameStart.timestamp();
                    float delta = 0.0F;
                    
                    if (end != start) {
                        delta = Mth.clamp(current / (frameEnd.timestamp() - frameStart.timestamp()), 0.0F, 1.0F);
                    }
                    
                    frameEnd.interpolation().apply(cache, delta, keyframes, start, end, speed);
                    channel.target().apply(part, cache);
                });
            });
        }
        
    }
    
    private static float getElapsedSeconds(AnimationDefinition definition, long progress) {
        float time = (float) progress / 1000.0F;
        return definition.looping() ? time % definition.lengthInSeconds() : time;
    }
    
    public static Vector3f posVec(float x, float y, float z) {
        return new Vector3f(x, -y, z);
    }
    
    public static Vector3f degreeVec(float xRot, float yRot, float zRot) {
        float toRadians = (float) Math.PI / 180;
        return new Vector3f(xRot * toRadians, yRot * toRadians, zRot * toRadians);
    }
    
    public static Vector3f scaleVec(double xScale, double yScale, double zScale) {
        return new Vector3f((float) (xScale - 1.0), (float) (yScale - 1.0), (float) (zScale - 1.0));
    }
}