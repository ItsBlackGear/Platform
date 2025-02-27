package com.blackgear.platform.client.animator.base;

import com.blackgear.platform.client.renderer.model.animation.AnimationChannel.Target;
import com.mojang.math.Vector3f;

import java.util.function.Function;

public class AnimatedPoint {
    private final Target target;
    private final Function<Float, Float> x;
    private final Function<Float, Float> y;
    private final Function<Float, Float> z;

    public AnimatedPoint(Target target, Function<Float, Float> x, Function<Float, Float> y, Function<Float, Float> z) {
        this.target = target;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AnimatedPoint(Target target, Function<Float, Vector3f> vector) {
        this(target, ticks -> vector.apply(ticks).x(), ticks -> vector.apply(ticks).y(), ticks -> vector.apply(ticks).z());
    }

    public Target target() {
        return target;
    }

    public float getX(float ticks) {
        return x.apply(ticks);
    }

    public float getY(float ticks) {
        return y.apply(ticks);
    }

    public float getZ(float ticks) {
        return z.apply(ticks);
    }
}