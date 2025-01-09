package com.blackgear.platform.client.renderer.model.geom;

import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class ModelLayers {
    private static final String DEFAULT_LAYER = "main";
    public static final Set<ModelLayerLocation> ALL_MODELS = Sets.<ModelLayerLocation>newHashSet();
    
    public static ModelLayerLocation register(ResourceLocation model) {
        return register(model, DEFAULT_LAYER);
    }
    
    public static ModelLayerLocation register(ResourceLocation model, String layer) {
        ModelLayerLocation location = new ModelLayerLocation(model, layer);
        
        if (!ALL_MODELS.add(location)) {
            throw new IllegalStateException("Duplicate registration for " + location);
        } else {
            return location;
        }
    }
    
    public static Stream<ModelLayerLocation> getKnownLocations() {
        return ALL_MODELS.stream();
    }
}