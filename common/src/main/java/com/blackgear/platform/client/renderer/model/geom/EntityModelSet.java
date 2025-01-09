package com.blackgear.platform.client.renderer.model.geom;

import com.blackgear.platform.client.RendererRegistry;
import com.blackgear.platform.client.renderer.model.geom.builder.LayerDefinition;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class EntityModelSet implements ResourceManagerReloadListener {
    public static final EntityModelSet INSTANCE = new EntityModelSet();
    private Map<ModelLayerLocation, LayerDefinition> roots = ImmutableMap.of();
    
    public NeoModelPart bakeLayer(ModelLayerLocation location) {
        LayerDefinition layer = RendererRegistry.MODEL_PROVIDERS.get(location).createLayerDefinition();
        
        if (layer == null) {
            throw new IllegalArgumentException("No model for layer " + location);
        } else {
            return layer.bakeRoot();
        }
    }
    
    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        this.roots = ImmutableMap.copyOf(LayerDefinitions.createRoots());
    }
}
