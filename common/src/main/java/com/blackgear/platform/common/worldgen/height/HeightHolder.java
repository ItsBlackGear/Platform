package com.blackgear.platform.common.worldgen.height;

public interface HeightHolder {
    default int minY() {
        return 0;
    }
    
    default int genDepth() {
        return 256;
    }
}