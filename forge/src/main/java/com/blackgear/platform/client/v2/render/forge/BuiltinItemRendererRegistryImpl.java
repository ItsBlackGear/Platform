package com.blackgear.platform.client.v2.render.forge;

import com.blackgear.platform.client.v2.render.BuiltinItemRendererRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
        if (item != null && item != Items.AIR) RENDERERS.putIfAbsent(item.asItem(), renderer);
    }

    @Override
    public Renderer get(ItemLike item) {
        return RENDERERS.get(item.asItem());
    }
}