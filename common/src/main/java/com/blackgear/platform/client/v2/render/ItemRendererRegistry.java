package com.blackgear.platform.client.v2.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemRendererRegistry {
    private static final Map<Item, ModelResourceLocation> MODELS = new ConcurrentHashMap<>();

    @ExpectPlatform
    public static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        throw new AssertionError();
    }

    public static void registerHandModel(ItemLike item, ModelResourceLocation handModel) {
        MODELS.put(item.asItem(), handModel);
    }

    public static ModelResourceLocation getHandModel(Item item) {
        return MODELS.get(item);
    }

    public interface DynamicItemRenderer {
        void render(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int packedLight, int combinedOverlay);
    }
}