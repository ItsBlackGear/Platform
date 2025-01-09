package com.blackgear.platform.client.renderer;

import com.blackgear.platform.client.renderer.model.NeoEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public interface NeoRenderLayerParent<T extends Entity, M extends NeoEntityModel<T>> {
    M getModel();
    
    ResourceLocation getTextureLocation(T entity);
}
