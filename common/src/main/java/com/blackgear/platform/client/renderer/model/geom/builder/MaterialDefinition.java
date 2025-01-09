package com.blackgear.platform.client.renderer.model.geom.builder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MaterialDefinition {
    final int xTexSize;
    final int yTexSize;
    
    public MaterialDefinition(int xTexSize, int yTexSize) {
        this.xTexSize = xTexSize;
        this.yTexSize = yTexSize;
    }
}
