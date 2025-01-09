package com.blackgear.platform.common.entity.resource;

import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class AnimationState {
    private long lastTime = Long.MAX_VALUE;
    private long accumulatedTime;
    
    public void start(int ticks) {
        this.lastTime = (long) ticks * 1000L / 20L;
        this.accumulatedTime = 0L;
    }
    
    public void startIfStopped(int ticks) {
        if (!this.isStarted()) {
            this.start(ticks);
        }
    }
    
    public void animateWhen(boolean condition, int ticks) {
        if (condition) {
            this.startIfStopped(ticks);
        } else {
            this.stop();
        }
    }
    
    public void stop() {
        this.lastTime = Long.MAX_VALUE;
    }
    
    public void ifStarted(Consumer<AnimationState> consumer) {
        if (this.isStarted()) {
            consumer.accept(this);
        }
    }
    
    public void updateTime(float delta, float speedMultiplier) {
        if (this.isStarted()) {
            long currentTime = Mth.lfloor(delta * 1000.0F / 20.0F);
            this.accumulatedTime += (long)((float)(currentTime - this.lastTime) * speedMultiplier);
            this.lastTime = currentTime;
        }
    }
    
    public void fastForward(int ticks, float speedMultiplier) {
        if (this.isStarted()) {
            this.accumulatedTime += (long) ((float) (ticks * 1000) * speedMultiplier) / 20L;
        }
    }
    
    public long getAccumulatedTime() {
        return this.accumulatedTime;
    }
    
    public boolean isStarted() {
        return this.lastTime != Long.MAX_VALUE;
    }
}