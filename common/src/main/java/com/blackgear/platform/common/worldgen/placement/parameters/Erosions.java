package com.blackgear.platform.common.worldgen.placement.parameters;

import net.minecraft.world.level.biome.Climate;

public enum Erosions {
    EROSION_0(Climate.Parameter.span(-1.0F, -0.78F)),
    EROSION_1(Climate.Parameter.span(-0.78F, -0.375F)),
    EROSION_2(Climate.Parameter.span(-0.375F, -0.2225F)),
    EROSION_3(Climate.Parameter.span(-0.2225F, 0.05F)),
    EROSION_4(Climate.Parameter.span(0.05F, 0.45F)),
    EROSION_5(Climate.Parameter.span(0.45F, 0.55F)),
    EROSION_6(Climate.Parameter.span(0.55F, 1.0F));

    private final Climate.Parameter parameter;

    Erosions(Climate.Parameter parameter) {
        this.parameter = parameter;
    }

    public Climate.Parameter point() {
        return this.parameter;
    }

    public static Climate.Parameter span(Erosions min, Erosions max) {
        return Climate.Parameter.span(min.point(), max.point());
    }
}