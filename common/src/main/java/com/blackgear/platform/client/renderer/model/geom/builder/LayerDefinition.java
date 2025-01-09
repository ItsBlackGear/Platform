package com.blackgear.platform.client.renderer.model.geom.builder;

import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LayerDefinition {
    private final MeshDefinition mesh;
    private final MaterialDefinition material;
    
    private LayerDefinition(MeshDefinition mesh, MaterialDefinition material) {
        this.mesh = mesh;
        this.material = material;
    }
    
    public NeoModelPart bakeRoot() {
        return this.mesh.getRoot().bake(this.material.xTexSize, this.material.yTexSize);
    }
    
    public static LayerDefinition create(MeshDefinition mesh, int xTexSize, int yTexSize) {
        return new LayerDefinition(mesh, new MaterialDefinition(xTexSize, yTexSize));
    }
}
