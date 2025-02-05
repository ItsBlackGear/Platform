package com.blackgear.platform.common.item;

import com.blackgear.platform.client.RendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
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
    public static final Map<Predicate<ItemStack>, ModelResourceLocation> HELD_MODELS = new ConcurrentHashMap<>();
    public static final Map<BiPredicate<ItemStack, ItemTransforms.TransformType>, ModelResourceLocation> GUI_MODELS = new ConcurrentHashMap<>();
    public static final Predicate<ItemTransforms.TransformType> SIMPLE_TRANSFORM = type -> type == ItemTransforms.TransformType.FIXED || type == ItemTransforms.TransformType.GUI || type == ItemTransforms.TransformType.GROUND;

    public static void registerItemModel(
        Predicate<ItemStack> item,
        Predicate<ItemTransforms.TransformType> transforms,
        ModelResourceLocation guiModel,
        ModelResourceLocation heldModel
    ) {
        HELD_MODELS.put(item, heldModel);
        GUI_MODELS.put((stack, type) -> item.test(stack) && transforms.test(type), guiModel);
        RendererRegistry.registerSpecialModels(heldModel, guiModel);
    }

    public static void registerItemModel(
        Predicate<ItemStack> item,
        ModelResourceLocation guiModel,
        ModelResourceLocation heldModel
    ) {
        registerItemModel(item, SIMPLE_TRANSFORM, guiModel, heldModel);
    }

    public static BakedModel modifyItemRendering(ItemStack stack, ItemTransforms.TransformType transform) {
        ModelManager manager = Minecraft.getInstance().getModelManager();
        return GUI_MODELS.entrySet()
            .stream()
            .filter(entry -> entry.getKey().test(stack, transform))
            .map(entry -> manager.getModel(entry.getValue()))
            .findFirst()
            .orElse(null);
    }
}