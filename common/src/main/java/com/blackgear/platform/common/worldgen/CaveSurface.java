package com.blackgear.platform.common.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CaveSurface implements StringRepresentable {
    CEILING(Direction.UP, 1, "ceiling"),
    FLOOR(Direction.DOWN, -1, "floor");
    
    public static final Codec<CaveSurface> CODEC = StringRepresentable.fromEnum(CaveSurface::values, CaveSurface::byName);
    private static final Map<String, CaveSurface> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(CaveSurface::getSerializedName, surface -> surface));
    private final Direction direction;
    private final int y;
    private final String id;
    
    CaveSurface(Direction direction, int y, String id) {
        this.direction = direction;
        this.y = y;
        this.id = id;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    public int getY() {
        return this.y;
    }
    
    @Override
    public String getSerializedName() {
        return this.id;
    }
    
    public String getName() {
        return this.id;
    }
    
    public static CaveSurface byName(String name) {
        return BY_NAME.get(name);
    }
}