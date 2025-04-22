package com.blackgear.platform.client.animator;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;

import java.util.*;

@Environment(EnvType.CLIENT)
public record MathAnimator(Map<String, List<AnimatedChannel>> animationsByBone) {
    public static void animate(HierarchicalModel<?> model, float animationProgress, MathAnimator builder) {
        float animTime = animationProgress / 20F;
        Vector3f vector = new Vector3f();

        for (Map.Entry<String, List<AnimatedChannel>> animation : builder.animationsByBone().entrySet()) {
            Optional<ModelPart> entry = model.getAnyDescendantWithName(animation.getKey());
            if (entry.isEmpty()) continue;

            ModelPart part = entry.get();
            List<AnimatedChannel> channels = animation.getValue();

            for (AnimatedChannel channel : channels) {
                for (AnimatedPoint point : channel.targets()) {
                    vector.set(point.getX(animTime), point.getY(animTime), point.getZ(animTime));
                    point.target().apply(part, vector);
                }
            }
        }
    }

    public static void animate(AnimatedModel model, float animationProgress, MathAnimator builder) {
        float animTime = animationProgress / 20F;
        Vector3f vector = new Vector3f();

        for (Map.Entry<String, List<AnimatedChannel>> animation : builder.animationsByBone().entrySet()) {
            Optional<ModelPart> entry = model.getAnyDescendantWithName(animation.getKey());
            if (entry.isEmpty()) continue;

            ModelPart part = entry.get();
            List<AnimatedChannel> channels = animation.getValue();

            for (AnimatedChannel channel : channels) {
                for (AnimatedPoint point : channel.targets()) {
                    vector.set(point.getX(animTime), point.getY(animTime), point.getZ(animTime));
                    point.target().apply(part, vector);
                }
            }
        }
    }

    public static class Builder {
        private final Map<String, List<AnimatedChannel>> animationByBone = Maps.newHashMap();

        public Builder addAnimation(String bone, AnimatedChannel animation) {
            this.animationByBone.computeIfAbsent(bone, key -> new ArrayList<>()).add(animation);
            return this;
        }

        public MathAnimator build() {
            Map<String, List<AnimatedChannel>> map = new HashMap<>();
            this.animationByBone.forEach((key, channels) -> map.put(key, List.copyOf(channels)));
            return new MathAnimator(Map.copyOf(map));
        }
    }
}