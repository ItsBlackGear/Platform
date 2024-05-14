package com.blackgear.platform.common.worldgen.placement.parameters;

import net.minecraft.world.level.biome.Climate.Parameter;

public enum Humidities {
    ARID(Parameter.span(-1.0F, -0.35F)),
    DRY(Parameter.span(-0.35F, -0.1F)),
    NEUTRAL(Parameter.span(-0.1F, 0.1F)),
    WET(Parameter.span(0.1F, 0.35F)),
    HUMID(Parameter.span(0.35F, 1.0F));
    
    private final Parameter parameter;
    
    Humidities(Parameter parameter) {
        this.parameter = parameter;
    }
    
    public Parameter point() {
        return this.parameter;
    }
    
    public static Parameter span(Humidities min, Humidities max) {
        return Parameter.span(min.point(), max.point());
    }
}