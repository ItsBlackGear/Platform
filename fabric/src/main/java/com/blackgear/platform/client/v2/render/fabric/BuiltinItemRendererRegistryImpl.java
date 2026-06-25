package com.blackgear.platform.client.v2.render.fabric;

import com.blackgear.platform.client.v2.render.BuiltinItemRendererRegistry;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public class BuiltinItemRendererRegistryImpl extends BuiltinItemRendererRegistry {
    private static final BuiltinItemRendererRegistry INSTANCE = new BuiltinItemRendererRegistryImpl();

    public static BuiltinItemRendererRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(ItemLike item, DynamicItemRenderer renderer) {
        net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.register(item.asItem(), renderer::render);
    }

    @Override
    public DynamicItemRenderer get(ItemLike item) {
        return Objects.requireNonNull(net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.get(item))::render;
    }
}