package com.blackgear.platform.client.resource;

import com.blackgear.platform.core.mixin.client.access.CameraAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/**
 * Represents a plane in 3D space defined by three vectors: forward, left, and up.
 */
@Environment(EnvType.CLIENT)
public class NearPlane {
    public final Vec3 forward;
    public final Vec3 left;
    public final Vec3 up;
    
    public NearPlane(Vec3 forward, Vec3 left, Vec3 up) {
        this.forward = forward;
        this.left = left;
        this.up = up;
    }
    
    /**
     * Calculates the top left point on the plane by adding the up and left vectors to the forward vector.
     *
     * @return Vec3 representing the top left point on the plane.
     */
    public Vec3 getTopLeft() {
        return this.forward.add(this.up).add(this.left);
    }
    
    /**
     * Calculates the top right point on the plane by adding the up vector and subtracting the left vector from the forward vector.
     *
     * @return Vec3 representing the top right point on the plane.
     */
    public Vec3 getTopRight() {
        return this.forward.add(this.up).subtract(this.left);
    }
    
    /**
     * Calculates the bottom left point on the plane by subtracting the up vector and adding the left vector from the forward vector.
     *
     * @return Vec3 representing the bottom left point on the plane.
     */
    public Vec3 getBottomLeft() {
        return this.forward.subtract(this.up).add(this.left);
    }
    
    /**
     * Calculates the bottom right point on the plane by subtracting both the up and left vectors from the forward vector.
     *
     * @return Vec3 representing the bottom right point on the plane.
     */
    public Vec3 getBottomRight() {
        return this.forward.subtract(this.up).subtract(this.left);
    }
    
    /**
     * Calculates a point on the plane by scaling the up and left vectors and adding/subtracting them from the forward vector.
     *
     * @param leftScale The scale factor for the left vector.
     * @param upScale The scale factor for the up vector.
     * @return Vec3 representing a point on the plane.
     */
    public Vec3 getPointOnPlane(float leftScale, float upScale) {
        return this.forward.add(this.up.scale(upScale)).subtract(this.left.scale(leftScale));
    }
    
    /**
     * calculates and returns a NearPlane object based on the current state of the provided Camera object.
     * The NearPlane object represents a plane in 3D space defined by three vectors: forward, left, and up.
     *
     * @param camera The Camera object from which the NearPlane is calculated.
     * @return A NearPlane object representing a plane in 3D space.
     */
    public static NearPlane getNearPlane(Camera camera) {
        CameraAccessor access = (CameraAccessor) camera;
        Minecraft minecraft = Minecraft.getInstance();
        double aspectRatio = (double) minecraft.getWindow().getWidth() / (double) minecraft.getWindow().getHeight();
        double halfFOVScaled = Math.tan(minecraft.options.fov().get() * (float) (Math.PI / 180.0) / 2.0) * 0.05F;
        double scaledAspectRatio = halfFOVScaled * aspectRatio;
        Vec3 forward = new Vec3(access.getForwards()).scale(0.05F);
        Vec3 left = new Vec3(access.getLeft()).scale(scaledAspectRatio);
        Vec3 up = new Vec3(access.getUp()).scale(halfFOVScaled);
        return new NearPlane(forward, left, up);
    }
}