package com.blackgear.platform.client.renderer.model.geom.builder;

import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import com.blackgear.platform.client.renderer.model.geom.PartPose;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class PartDefinition {
    private final List<CubeDefinition> cubes;
    private final PartPose partPose;
    private final Map<String, PartDefinition> children = Maps.newHashMap();
    
    PartDefinition(List<CubeDefinition> cubes, PartPose pose) {
        this.cubes = cubes;
        this.partPose = pose;
    }
    
    public PartDefinition addOrReplaceChild(String partName, CubeListBuilder builder, PartPose pose) {
        PartDefinition definition = new PartDefinition(builder.getCubes(), pose);
        PartDefinition stored = this.children.put(partName, definition);

        if (stored != null) {
            definition.children.putAll(stored.children);
        }
        
        return definition;
    }
    
    public NeoModelPart bake(int texWidth, int texHeight) {
        Object2ObjectArrayMap<String, NeoModelPart> children = this.children
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().bake(texWidth, texHeight),
                    (part, partx) -> part,
                    Object2ObjectArrayMap::new
                )
            );
        List<NeoModelPart.Cube> cubes = this.cubes.stream()
            .map(cubeDefinition -> cubeDefinition.bake(texWidth, texHeight))
            .collect(ImmutableList.toImmutableList());

        NeoModelPart part = new NeoModelPart(cubes, children);
        part.setInitialPose(this.partPose);
        part.loadPose(this.partPose);
        return part;
    }
    
    public PartDefinition getChild(String string) {
        return this.children.get(string);
    }
}
