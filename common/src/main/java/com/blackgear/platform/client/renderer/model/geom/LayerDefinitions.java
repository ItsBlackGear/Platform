package com.blackgear.platform.client.renderer.model.geom;

import com.blackgear.platform.client.RendererRegistry;
import com.blackgear.platform.client.renderer.model.geom.builder.LayerDefinition;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class LayerDefinitions {
    public static Map<ModelLayerLocation, LayerDefinition> createRoots() {
        ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder = ImmutableMap.builder();

        for (Map.Entry<ModelLayerLocation, RendererRegistry.ModelLayerProvider> entry : RendererRegistry.MODEL_PROVIDERS.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().createLayerDefinition());
        }
        
        ImmutableMap<ModelLayerLocation, LayerDefinition> roots = builder.build();
        List<ModelLayerLocation> missingDefinitions = ModelLayers.getKnownLocations()
            .filter(location -> !roots.containsKey(location))
            .collect(Collectors.toList());

        if (!missingDefinitions.isEmpty()) {
            throw new RuntimeException("Missing layer definitions: " + missingDefinitions);
        } else {
            return roots;
        }
    }
}