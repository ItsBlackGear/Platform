package com.blackgear.platform.client.animator.base;

public class AnimatedChannel {
    public final AnimatedPoint[] targets;

    public AnimatedChannel(AnimatedPoint... targets) {
        this.targets = targets;
    }
}