package com.blackgear.platform.client.event;

import com.blackgear.platform.core.util.event.CancellableResult;
import com.blackgear.platform.core.util.event.Event;
import com.mojang.blaze3d.shaders.FogShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;

@Environment(EnvType.CLIENT)
public interface FogRendering {
    Event<FogDensity> FOG_DENSITY = Event.create(FogDensity.class, densities -> (camera, density) -> {
        for (FogDensity callback : densities) {
            return callback.setDensity(camera, density);
        }

        return density;
    });
    Event<FogColor> FOG_COLOR = Event.create(FogColor.class);
    Event<FogRender> FOG_RENDER = Event.cancellable(FogRender.class);

    interface FogDensity {
        float setDensity(Camera camera, float density);
    }

    interface FogColor {
        void setColor(ColorData data, float partialTicks);
    }

    interface FogRender {
        CancellableResult onFogRender(FogRenderer.FogMode mode, FogType type, Camera camera, float partialTick, float renderDistance, float nearDistance, float farDistance, FogShape shape, FogData fogData);
    }

    class FogData {
        private float farPlaneDistance;
        private float nearPlaneDistance;
        private FogShape shape;

        public FogData(float nearPlaneDistance, float farPlaneDistance, FogShape shape) {
            this.farPlaneDistance = farPlaneDistance;
            this.nearPlaneDistance = nearPlaneDistance;
            this.shape = shape;
        }

        public float getFarPlaneDistance() {
            return this.farPlaneDistance;
        }

        public float getNearPlaneDistance() {
            return this.nearPlaneDistance;
        }

        public FogShape getShape() {
            return this.shape;
        }

        public void setFarPlaneDistance(float farPlaneDistance) {
            this.farPlaneDistance = farPlaneDistance;
        }

        public void setNearPlaneDistance(float nearPlaneDistance) {
            this.nearPlaneDistance = nearPlaneDistance;
        }

        public void setShape(FogShape shape) {
            this.shape = shape;
        }
    }

    class ColorData {
        private final Camera camera;
        private float red;
        private float green;
        private float blue;

        public ColorData(Camera camera, float red, float green, float blue) {
            this.camera = camera;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public Camera getCamera() {
            return this.camera;
        }

        public float getRed() {
            return this.red;
        }

        public float getGreen() {
            return this.green;
        }

        public float getBlue() {
            return this.blue;
        }

        public void setRed(float red) {
            this.red = red;
        }

        public void setGreen(float green) {
            this.green = green;
        }

        public void setBlue(float blue) {
            this.blue = blue;
        }
    }
}