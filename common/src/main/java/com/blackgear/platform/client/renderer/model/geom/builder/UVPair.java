package com.blackgear.platform.client.renderer.model.geom.builder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UVPair {
    private final float u;
    private final float v;
    
    public UVPair(float u, float v) {
        this.u = u;
        this.v = v;
    }
    
    public float u() {
        return this.u;
    }
    
    public float v() {
        return this.v;
    }
    
    public String toString() {
        return "(" + this.u + "," + this.v + ")";
    }
}
