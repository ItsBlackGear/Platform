package com.blackgear.platform.client.v2.render;

import com.blackgear.platform.core.util.event.ResultHolder;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemRendererRegistry {
    public static final Supplier<ItemRendererRegistry> INSTANCE = Suppliers.memoize(ItemRendererRegistry::new);
    private static final Map<Item, Renderer> RENDERERS = new HashMap<>();

    public void register(ItemLike item, Renderer renderer) {
        RENDERERS.putIfAbsent(item.asItem(), renderer);
    }

    public Renderer get(ItemLike item) {
        return RENDERERS.get(item.asItem());
    }

    public Map<Item, Renderer> getRenderers() {
        return RENDERERS;
    }

    public interface Renderer {
        ResultHolder<BakedModel> renderFirstPerson(ItemStack stack, ItemDisplayContext context, ItemModelShaper shaper);

        ResultHolder<BakedModel> renderThirdPerson(ItemStack stack, ItemModelShaper shaper);

        Set<ModelResourceLocation> registerModels();

        default boolean shouldUse() {
            return true;
        }
    }
}