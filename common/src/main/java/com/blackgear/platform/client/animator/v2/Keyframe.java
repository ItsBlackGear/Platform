package com.blackgear.platform.client.animator.v2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public record Keyframe(float timestamp, AnimationChannel.Value target, AnimationChannel.Interpolation interpolation) {
    public Keyframe(AnimationChannel.Value target) {
        this(0.0F, target, AnimationChannel.Interpolations.LINEAR);
    }

    public Keyframe(float timestamp, Vector3f target, AnimationChannel.Interpolation interpolation) {
        this(timestamp, query -> target, interpolation);
    }

    public Keyframe(Vector3f target) {
        this(0.0F, target, AnimationChannel.Interpolations.LINEAR);
    }
}