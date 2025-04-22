package com.blackgear.platform.common.worldgen.placement.parameters;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.Parameter;

public enum Humidity {
    ARID(Parameter.span(-1.0F, -0.35F)),
    DRY(Parameter.span(-0.35F, -0.1F)),
    NEUTRAL(Parameter.span(-0.1F, 0.1F)),
    WET(Parameter.span(0.1F, 0.3F)),
    HUMID(Parameter.span(0.3F, 1.0F)),
    FULL_RANGE(Parameter.span(-1.0F, 1.0F));
    
    private final Parameter parameter;
    
    Humidity(Parameter parameter) {
        this.parameter = parameter;
    }
    
    public Parameter parameter() {
        return this.parameter;
    }
    
    public static Parameter span(Humidity min, Humidity max) {
        return Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}