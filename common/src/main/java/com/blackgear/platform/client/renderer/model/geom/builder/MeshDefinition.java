package com.blackgear.platform.client.renderer.model.geom.builder;

import com.blackgear.platform.client.renderer.model.geom.PartPose;
import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MeshDefinition {
    private final PartDefinition root = new PartDefinition(ImmutableList.of(), PartPose.ZERO);
    
    public PartDefinition getRoot() {
        return this.root;
    }
}