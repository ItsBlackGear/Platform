package com.blackgear.platform.common.worldgen.placement.parameters;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.Parameter;

public enum Weirdness {
    MID_SLICE_NORMAL_ASCENDING(Parameter.span(-1.0F, -0.93333334F)),
    HIGH_SLICE_NORMAL_ASCENDING(Parameter.span(-0.93333334F, -0.7666667F)),
    PEAK_NORMAL(Parameter.span(-0.7666667F, -0.56666666F)),
    HIGH_SLICE_NORMAL_DESCENDING(Parameter.span(-0.56666666F, -0.4F)),
    MID_SLICE_NORMAL_DESCENDING(Parameter.span(-0.4F, -0.26666668F)),
    LOW_SLICE_NORMAL_DESCENDING(Parameter.span(-0.26666668F, -0.05F)),
    VALLEY(Parameter.span(-0.05F, 0.05F)),
    LOW_SLICE_VARIANT_ASCENDING(Parameter.span(0.05F, 0.26666668F)),
    MID_SLICE_VARIANT_ASCENDING(Parameter.span(0.26666668F, 0.4F)),
    HIGH_SLICE_VARIANT_ASCENDING(Parameter.span(0.4F, 0.56666666F)),
    PEAK_VARIANT(Parameter.span(0.56666666F, 0.7666667F)),
    HIGH_SLICE_VARIANT_DESCENDING(Parameter.span(0.7666667F, 0.93333334F)),
    MID_SLICE_VARIANT_DESCENDING(Parameter.span(0.93333334F, 1.0F)),
    FULL_RANGE(Parameter.span(-1.0F, 1.0F));
    
    private final Parameter parameter;
    
    Weirdness(Parameter parameter) {
        this.parameter = parameter;
    }
    
    public Parameter parameter() {
        return this.parameter;
    }
    
    public static Parameter span(Weirdness min, Weirdness max) {
        return Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}