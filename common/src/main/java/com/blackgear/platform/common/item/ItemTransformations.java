package com.blackgear.platform.common.item;

import com.blackgear.platform.client.RendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class ItemTransformations {
    public static final Map<Predicate<ItemStack>, ModelResourceLocation> MODELS_IN_HAND = new ConcurrentHashMap<>();
    public static final Map<BiPredicate<ItemStack, TransformType>, ModelResourceLocation> MODELS_IN_INVENTORY = new ConcurrentHashMap<>();
    public static final Predicate<TransformType> INVENTORY = mode -> mode == TransformType.GUI || mode == TransformType.GROUND || mode == TransformType.FIXED;
    
    /**
     * Registers item models for both inventory and hand views.
     *
     * @param itemPredicate Predicate to match the item stack.
     * @param transformPredicate Predicate to match the transform type.
     * @param inventoryModel ModelResourceLocation for the inventory view.
     * @param heldModel ModelResourceLocation for the hand view.
     */
    public static void registerItemModel(
        Predicate<ItemStack> itemPredicate,
        Predicate<TransformType> transformPredicate,
        ModelResourceLocation inventoryModel,
        ModelResourceLocation heldModel
    ) {
        MODELS_IN_HAND.put(itemPredicate, heldModel);
        MODELS_IN_INVENTORY.put((stack, transform) -> itemPredicate.test(stack) && transformPredicate.test(transform), inventoryModel);
        RendererRegistry.registerSpecialModels(inventoryModel, heldModel);
    }
    
    /**
     * Registers item models for both inventory and hand views with default transform type predicate.
     *
     * @param itemPredicate Predicate to match the item stack.
     * @param inventoryModel ModelResourceLocation for the inventory view.
     * @param heldModel ModelResourceLocation for the hand view.
     */
    public static void registerItemModel(
        Predicate<ItemStack> itemPredicate,
        ModelResourceLocation inventoryModel,
        ModelResourceLocation heldModel
    ) {
        registerItemModel(itemPredicate, INVENTORY, inventoryModel, heldModel);
    }
    
    /**
     * Modifies the item rendering based on the item stack and transform type.
     *
     * @param stack The item stack.
     * @param transform The transform type.
     * @return The baked model for the item.
     */
    public static BakedModel modifyItemRendering(ItemStack stack, TransformType transform) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        return MODELS_IN_INVENTORY.entrySet()
            .stream()
            .filter(entry -> entry.getKey().test(stack, transform))
            .map(entry -> modelManager.getModel(entry.getValue()))
            .findFirst()
            .orElse(null);
    }
}