package com.blackgear.platform.client.animator;

import com.blackgear.platform.client.animator.base.AnimatedChannel;
import com.blackgear.platform.client.renderer.model.NeoHierarchicalModel;
import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MathAnimator {
    public final Map<String, List<AnimatedChannel>> animationsByBone;

    public MathAnimator(Map<String, List<AnimatedChannel>> animationsByBone) {
        this.animationsByBone = animationsByBone;
    }

    public static void animated(NeoHierarchicalModel<?> model, float animationProgress, MathAnimator builder) {
        float animTime = animationProgress / 20F;

        for (Map.Entry<String, List<AnimatedChannel>> animation : builder.animationsByBone.entrySet()) {
            Optional<NeoModelPart> entry = model.getAnyDescendantWithName(animation.getKey());
            List<AnimatedChannel> channels = animation.getValue();
            entry.ifPresent(part -> channels.forEach(channel -> Arrays.stream(channel.targets)
                .forEach(point -> {
                    Vector3f vector = new Vector3f(point.getX(animTime), point.getY(animTime), point.getZ(animTime));
                    point.target().apply(part, vector);
                })));
        }
    }

    public static class Builder {
        private final Map<String, List<AnimatedChannel>> animationByBone = Maps.newHashMap();

        public Builder addAnimation(String bone, AnimatedChannel animation) {
            this.animationByBone.computeIfAbsent(bone, key -> Lists.newArrayList()).add(animation);
            return this;
        }

        public MathAnimator build() {
            return new MathAnimator(this.animationByBone);
        }
    }
}