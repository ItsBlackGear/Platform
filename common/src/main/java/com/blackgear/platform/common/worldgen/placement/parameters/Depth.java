package com.blackgear.platform.common.worldgen.placement.parameters;

import net.minecraft.world.level.biome.Climate.Parameter;

public enum Depth {
    SURFACE(Parameter.point(0.0F)),
    UNDERGROUND(Parameter.span(0.2F, 0.9F)),
    FLOOR(Parameter.point(1.0F));

    private final Parameter parameter;

    Depth(Parameter parameter) {
        this.parameter = parameter;
    }

    public Parameter point() {
        return this.parameter;
    }

    public static Parameter span(Depth min, Depth max) {
        return Parameter.span(min.point(), max.point());
    }
}