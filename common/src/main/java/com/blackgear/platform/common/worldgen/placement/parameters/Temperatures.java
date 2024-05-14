package com.blackgear.platform.common.worldgen.placement.parameters;

import net.minecraft.world.level.biome.Climate.Parameter;

public enum Temperatures {
    ICY(Parameter.span(-1.0F, -0.45F)),
    COOL(Parameter.span(-0.45F, -0.15F)),
    NEUTRAL(Parameter.span(-0.15F, 0.2F)),
    WARM(Parameter.span(0.2F, 0.55F)),
    HOT(Parameter.span(0.55F, 1.0F));
    
    private final Parameter parameter;
    
    Temperatures(Parameter parameter) {
        this.parameter = parameter;
    }
    
    public Parameter point() {
        return this.parameter;
    }
    
    public static Parameter span(Temperatures min, Temperatures max) {
        return Parameter.span(min.point(), max.point());
    }
}