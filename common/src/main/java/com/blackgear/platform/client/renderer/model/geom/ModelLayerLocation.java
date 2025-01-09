package com.blackgear.platform.client.renderer.model.geom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ModelLayerLocation {
    private final ResourceLocation model;
    private final String layer;
    
    public ModelLayerLocation(ResourceLocation model, String layer) {
        this.model = model;
        this.layer = layer;
    }
    
    public ResourceLocation getModel() {
        return this.model;
    }
    
    public String getLayer() {
        return this.layer;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ModelLayerLocation)) {
            return false;
        } else {
            ModelLayerLocation location = (ModelLayerLocation) object;
            return this.model.equals(location.model) && this.layer.equals(location.layer);
        }
    }
    
    @Override
    public int hashCode() {
        int i = this.model.hashCode();
        return 31 * i + this.layer.hashCode();
    }
    
    @Override
    public String toString() {
        return this.model + "#" + this.layer;
    }
}