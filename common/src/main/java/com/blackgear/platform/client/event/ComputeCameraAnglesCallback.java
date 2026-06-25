package com.blackgear.platform.client.event;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

public interface ComputeCameraAnglesCallback {
    Event<ComputeCameraAnglesCallback> EVENT = Event.create(ComputeCameraAnglesCallback.class);

    void handle(ComputeCameraAngles event);

    class ComputeCameraAngles {
        private final GameRenderer renderer;
        private final Camera camera;
        private final double partialTick;
        private float yaw, pitch, roll;

        public ComputeCameraAngles(GameRenderer renderer, Camera camera, double partialTick, float yaw, float pitch, float roll) {
            this.renderer = renderer;
            this.camera = camera;
            this.partialTick = partialTick;
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public GameRenderer getRenderer() { return this.renderer; }
        public Camera getCamera() { return this.camera; }
        public double getPartialTick() { return this.partialTick; }

        public float getYaw() { return this.yaw; }
        public void setYaw(float yaw) { this.yaw = yaw; }

        public float getPitch() { return this.pitch; }
        public void setPitch(float pitch) { this.pitch = pitch; }

        public float getRoll() { return this.roll; }
        public void setRoll(float roll) { this.roll = roll; }
    }
}