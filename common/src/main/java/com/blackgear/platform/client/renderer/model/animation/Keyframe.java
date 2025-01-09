package com.blackgear.platform.client.renderer.model.animation;

import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Keyframe {
    private final float timestamp;
    private final Vector3f target;
    private final AnimationChannel.Interpolation interpolation;
    
    public Keyframe(float timestamp, Vector3f target, AnimationChannel.Interpolation interpolation) {
        this.timestamp = timestamp;
        this.target = target;
        this.interpolation = interpolation;
    }
    
    public float timestamp() {
        return this.timestamp;
    }
    
    public Vector3f target() {
        return this.target;
    }
    
    public AnimationChannel.Interpolation interpolation() {
        return this.interpolation;
    }
}