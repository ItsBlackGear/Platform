package com.blackgear.platform.client.renderer.model.animation;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class AnimationDefinition {
    private final float lengthInSeconds;
    private final boolean looping;
    private final Map<String, List<AnimationChannel>> boneAnimations;
    
    public AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {
        this.lengthInSeconds = lengthInSeconds;
        this.looping = looping;
        this.boneAnimations = boneAnimations;
    }
    
    public float lengthInSeconds() {
        return this.lengthInSeconds;
    }
    
    public boolean looping() {
        return this.looping;
    }
    
    public Map<String, List<AnimationChannel>> boneAnimations() {
        return this.boneAnimations;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final float length;
        private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
        private boolean looping;
        
        public static Builder withLength(float length) {
            return new Builder(length);
        }
        
        private Builder(float length) {
            this.length = length;
        }
        
        public Builder looping() {
            this.looping = true;
            return this;
        }
        
        public Builder addAnimation(String partName, AnimationChannel channel) {
            this.animationByBone.computeIfAbsent(partName, name -> {
                return new ArrayList<>();
            }).add(channel);
            return this;
        }
        
        public AnimationDefinition build() {
            return new AnimationDefinition(this.length, this.looping, this.animationByBone);
        }
    }
}