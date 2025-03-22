package com.blackgear.platform.client;

import com.blackgear.platform.client.renderer.model.geom.ModelLayerLocation;
import com.blackgear.platform.client.renderer.model.geom.builder.LayerDefinition;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public class RendererRegistry {
    @Deprecated
    public static void addBlockRenderType(RenderType type, Block... blocks) {
        GameRendering.registerBlockRenderers(event -> event.register(type, blocks));
    }

    @Deprecated
    public static void addFluidRenderType(RenderType type, Fluid... fluids) {
        GameRendering.registerBlockRenderers(event -> event.register(type, fluids));
    }

    @Deprecated @SafeVarargs
    public static void addItemColor(ItemColor color, Supplier<? extends ItemLike>... items) {
        Arrays.stream(items).forEach(supplier -> GameRendering.registerBlockColors(event -> event.register(color, supplier.get())));
    }

    @Deprecated @SafeVarargs
    public static void addBlockColor(BlockColor color, Supplier<? extends Block>... items) {
        Arrays.stream(items).forEach(supplier -> GameRendering.registerBlockColors(event -> event.register(color, supplier.get())));
    }

    @Deprecated
    public static <T extends Entity> void addEntityRenderer(Supplier<? extends EntityType<? extends T>> type, Function<EntityRenderDispatcher, EntityRenderer<T>> renderer) {
        GameRendering.registerEntityRenderers(event -> event.register(type.get(), renderer));
    }

    @Deprecated
    public static <T extends BlockEntity> void addBlockEntityRenderer(Supplier<BlockEntityType<T>> type, Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>> renderer) {
        GameRendering.registerBlockEntityRenderers(event -> event.register(type.get(), renderer));
    }

    @Deprecated
    public static void registerModelLayer(ModelLayerLocation model, LayerDefinition provider) {
        GameRendering.registerModelLayers(event -> event.register(model, provider));
    }

    @Deprecated
    public static void registerSpecialModel(ModelResourceLocation model) {
        GameRendering.registerSpecialModels(event -> event.register(model));
    }

    public static void registerSpecialModels(ModelResourceLocation... models) {
        GameRendering.registerSpecialModels(event -> event.register(models));
    }
}