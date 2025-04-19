package com.blackgear.platform.client.animator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public interface AnimatedModel {
    ModelPart root();

    default Optional<ModelPart> getAnyDescendantWithName(String name) {
        return root().getAllParts()
            .filter(part -> part.hasChild(name))
            .findFirst()
            .map(part -> part.getChild(name));
    }
}