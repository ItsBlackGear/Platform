package com.blackgear.platform.client.renderer.model.geom.builder;

import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Environment(EnvType.CLIENT)
public final class CubeDefinition {
    private final Vector3f origin;
    private final Vector3f dimensions;
    private final CubeDeformation grow;
    private final boolean mirror;
    private final UVPair texCoord;
    private final UVPair texScale;
    private final Set<Direction> visibleFaces;
    
    CubeDefinition(
        @Nullable String comment,
        float texCoordU,
        float texCoordV,
        float originX,
        float originY,
        float originZ,
        float dimensionX,
        float dimensionY,
        float dimensionZ,
        CubeDeformation grow,
        boolean mirror,
        float texScaleU,
        float texScaleV,
        Set<Direction> visibleFaces
    ) {
        this.texCoord = new UVPair(texCoordU, texCoordV);
        this.origin = new Vector3f(originX, originY, originZ);
        this.dimensions = new Vector3f(dimensionX, dimensionY, dimensionZ);
        this.grow = grow;
        this.mirror = mirror;
        this.texScale = new UVPair(texScaleU, texScaleV);
        this.visibleFaces = visibleFaces;
    }
    
    public NeoModelPart.Cube bake(int texWidth, int texHeight) {
        return new NeoModelPart.Cube(
            (int) this.texCoord.u(),
            (int) this.texCoord.v(),
            this.origin.x(),
            this.origin.y(),
            this.origin.z(),
            this.dimensions.x(),
            this.dimensions.y(),
            this.dimensions.z(),
            this.grow.growX,
            this.grow.growY,
            this.grow.growZ,
            this.mirror,
            (float) texWidth * this.texScale.u(),
            (float) texHeight * this.texScale.v(),
            this.visibleFaces
        );
    }
}