package com.blackgear.platform.common.worldgen;

import com.blackgear.platform.common.worldgen.height.HeightHolder;

public class WorldGenerationContext {
    private final int minY;
    private final int height;
    
    public WorldGenerationContext(HeightHolder holder) {
        this.minY = Math.max(0, holder.minY());
        this.height = Math.min(256, holder.genDepth());
    }
    
    public int getMinGenY() {
        return this.minY;
    }
    
    public int getGenDepth() {
        return this.height;
    }
}