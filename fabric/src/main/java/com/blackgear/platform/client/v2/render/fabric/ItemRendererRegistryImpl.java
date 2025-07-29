package com.blackgear.platform.client.v2.render.fabric;

import com.blackgear.platform.client.v2.render.ItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.world.level.ItemLike;

public class ItemRendererRegistryImpl {
    public static void registerRenderer(ItemLike item, ItemRendererRegistry.DynamicItemRenderer renderer) {
        BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::render);
    }
}