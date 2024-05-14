package com.blackgear.platform.client;

import com.blackgear.platform.client.resource.NearPlane;
import com.blackgear.platform.core.mixin.client.access.CameraAccessor;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class FogRenderingHandler {
    @ExpectPlatform
    public static void addFogRendering(FogRendering rendering) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void addFogColor(FogColor color) {
        throw new AssertionError();
    }
    
    protected static BlockState getStateOnCamera(Camera camera) {
        if (camera.isInitialized()) {
            NearPlane plane = NearPlane.getNearPlane(camera);
            CameraAccessor access = (CameraAccessor) camera;
            
            for (Vec3 vector : Arrays.asList(plane.forward, plane.getTopLeft(), plane.getTopRight(), plane.getBottomLeft(), plane.getBottomRight())) {
                Vec3 position = camera.getPosition().add(vector);
                return access.getLevel().getBlockState(new BlockPos(position));
            }
        }
        
        return Blocks.AIR.defaultBlockState();
    }
    
    public interface FogRendering {
        FogRenderingContext setupRendering(FogRenderingContext context);
    }
    
    public static class FogRenderingContext {
        private final Camera camera;
        private float farPlaneDistance;
        private FogRenderer.FogMode fogMode;
        private boolean cancellable = true;
        
        public FogRenderingContext(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance) {
            this.camera = camera;
            this.fogMode = fogMode;
            this.farPlaneDistance = farPlaneDistance;
        }
        
        public Camera getCamera() {
            return this.camera;
        }
        
        public void setFogMode(FogRenderer.FogMode fogMode) {
            this.fogMode = fogMode;
        }
        
        public FogRenderer.FogMode getFogMode() {
            return this.fogMode;
        }
        
        public void setFarPlaneDistance(float farPlaneDistance) {
            this.farPlaneDistance = farPlaneDistance;
        }
        
        public float getFarPlaneDistance() {
            return this.farPlaneDistance;
        }
        
        public void setCancellable(boolean cancellable) {
            this.cancellable = cancellable;
        }
        
        public boolean isCancellable() {
            return this.cancellable;
        }
        
        public BlockState getStateAtCamera() {
            return getStateOnCamera(this.camera);
        }
        
        public FogRenderingContext build() {
            return new FogRenderingContext(this.camera, this.fogMode, this.farPlaneDistance);
        }
    }
    
    public interface FogColor {
        FogColorContext setupColor(FogColorContext context);
    }
    
    public static class FogColorContext {
        private final Camera camera;
        private float red;
        private float green;
        private float blue;
        
        public FogColorContext(Camera camera, float red, float green, float blue) {
            this.camera = camera;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
        
        public Camera getCamera() {
            return this.camera;
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
        
        public float getRed() {
            return this.red;
        }
        
        public float getGreen() {
            return this.green;
        }
        
        public float getBlue() {
            return this.blue;
        }
        
        public BlockState getStateAtCamera() {
            return getStateOnCamera(this.camera);
        }
        
        public FogColorContext build() {
            return new FogColorContext(this.camera, this.red, this.green, this.blue);
        }
    }
}