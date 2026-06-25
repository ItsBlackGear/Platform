package com.blackgear.platform.client.animator.v2;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {
    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final float length;
        private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
        private boolean looping;

        public static Builder withLength(float lengthInSeconds) {
            return new Builder(lengthInSeconds);
        }

        private Builder(float lengthInSeconds) {
            this.length = lengthInSeconds;
        }

        public Builder looping() {
            this.looping = true;
            return this;
        }

        public Builder addAnimation(String bone, AnimationChannel animationChannel) {
            this.animationByBone.computeIfAbsent(bone, string -> new ArrayList<>()).add(animationChannel);
            return this;
        }

        public AnimationDefinition build() {
            return new AnimationDefinition(this.length, this.looping, this.animationByBone);
        }
    }
}
