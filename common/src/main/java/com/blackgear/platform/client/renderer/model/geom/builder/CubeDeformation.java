package com.blackgear.platform.client.renderer.model.geom.builder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CubeDeformation {
    public static final CubeDeformation NONE = new CubeDeformation(0.0F);
    final float growX;
    final float growY;
    final float growZ;
    
    public CubeDeformation(float growX, float growY, float growZ) {
        this.growX = growX;
        this.growY = growY;
        this.growZ = growZ;
    }
    
    public CubeDeformation(float grow) {
        this(grow, grow, grow);
    }
    
    public CubeDeformation extend(float scale) {
        return new CubeDeformation(this.growX + scale, this.growY + scale, this.growZ + scale);
    }
    
    public CubeDeformation extend(float xScale, float yScale, float zScale) {
        return new CubeDeformation(this.growX + xScale, this.growY + yScale, this.growZ + zScale);
    }
}