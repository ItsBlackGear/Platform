package com.blackgear.platform.common.worldgen.placement.parameters;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.Parameter;

public enum Continentalness {
    MUSHROOM_FIELDS(Parameter.span(-1.2F, -1.05F)),
    DEEP_OCEAN(Parameter.span(-1.05F, -0.455F)),
    OCEAN(Parameter.span(-0.455F, -0.19F)),
    COAST(Parameter.span(-0.19F, -0.11F)),
    NEAR_INLAND(Parameter.span(-0.11F, 0.03F)),
    MID_INLAND(Parameter.span(0.03F, 0.3F)),
    FAR_INLAND(Parameter.span(0.03F, 1.0F)),
    INLAND(Parameter.span(-0.11F, 0.55F)),
    FULL_RANGE(Parameter.span(-1.2F, 1.0F));

    private final Parameter parameter;
    
    Continentalness(Parameter parameter) {
        this.parameter = parameter;
    }
    
    public Parameter parameter() {
        return this.parameter;
    }
    
    public static Parameter span(Continentalness min, Continentalness max) {
        return Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}