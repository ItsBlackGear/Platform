package com.blackgear.platform.client.v2.render.forge;

import com.blackgear.platform.client.v2.render.BuiltinItemRendererRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.Map;

public class BuiltinItemRendererRegistryImpl extends BuiltinItemRendererRegistry {
    public static final Map<Item, Renderer> RENDERERS = new HashMap<>();
    private static final BuiltinItemRendererRegistry INSTANCE = new BuiltinItemRendererRegistryImpl();

    public static BuiltinItemRendererRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(ItemLike item, Renderer renderer) {
        if (RENDERERS.putIfAbsent(item.asItem(), renderer) != null) {
            throw new IllegalArgumentException("Item " + BuiltInRegistries.ITEM.getKey(item.asItem()) + " already has a dynamic rendering!");
        }
    }

    @Override
    public Renderer get(ItemLike item) {
        return RENDERERS.get(item.asItem());
    }
}