package com.blackgear.platform.common.worldgen.placement.parameters;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.Parameter;

public enum Temperature {
    ICY(Parameter.span(-1.0F, -0.45F)),
    COOL(Parameter.span(-0.45F, -0.15F)),
    NEUTRAL(Parameter.span(-0.15F, 0.2F)),
    WARM(Parameter.span(0.2F, 0.55F)),
    HOT(Parameter.span(0.55F, 1.0F)),
    FROZEN(Parameter.span(-1.0F, -0.45F)),
    UNFROZEN(Parameter.span(-0.45F, 1.0F)),
    FULL_RANGE(Parameter.span(-1.0F, 1.0F));
    
    private final Parameter parameter;
    
    Temperature(Parameter parameter) {
        this.parameter = parameter;
    }
    
    public Parameter parameter() {
        return this.parameter;
    }
    
    public static Parameter span(Temperature min, Temperature max) {
        return Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}