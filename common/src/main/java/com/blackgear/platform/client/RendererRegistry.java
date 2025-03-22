package com.blackgear.platform.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.function.Supplier;

public class RendererRegistry {
    @Deprecated(forRemoval = true)
    public static void addBlockRenderType(RenderType type, Block... blocks) {
        GameRendering.registerBlockRenderers(event -> event.register(type, blocks));
    }

    @Deprecated(forRemoval = true)
    public static void addFluidRenderType(RenderType type, Fluid... fluids) {
        GameRendering.registerBlockRenderers(event -> event.register(type, fluids));
    }

    @SafeVarargs @Deprecated(forRemoval = true)
    public static void addItemColor(ItemColor color, Supplier<? extends ItemLike>... items) {
        Arrays.stream(items).forEach(supplier -> GameRendering.registerBlockColors(event -> event.register(color, supplier.get())));
    }

    @SafeVarargs @Deprecated(forRemoval = true)
    public static void addBlockColor(BlockColor color, Supplier<? extends Block>... items) {
        Arrays.stream(items).forEach(supplier -> GameRendering.registerBlockColors(event -> event.register(color, supplier.get())));
    }

    @Deprecated(forRemoval = true)
    public static <T extends Entity> void addEntityRenderer(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> renderer) {
        GameRendering.registerEntityRenderers(event -> event.register(type.get(), renderer));
    }

    @Deprecated(forRemoval = true)
    public static <T extends BlockEntity> void addBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> renderer) {
        GameRendering.registerBlockEntityRenderers(event -> event.register(type.get(), renderer));
    }

    @Deprecated(forRemoval = true)
    public static void addLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        GameRendering.registerModelLayers(event -> event.register(layer, definition));
    }

    @Deprecated(forRemoval = true)
    public static void registerSpecialModel(ModelResourceLocation model) {
        GameRendering.registerSpecialModels(event -> event.register(model));
    }

    @Deprecated(forRemoval = true)
    public static void registerSpecialModels(ModelResourceLocation... models) {
        GameRendering.registerSpecialModels(event -> event.register(models));
    }
}