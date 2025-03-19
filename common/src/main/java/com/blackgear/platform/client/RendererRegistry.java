package com.blackgear.platform.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class RendererRegistry {
    public static final Set<ModelResourceLocation> SPECIAL_MODELS = ConcurrentHashMap.newKeySet();

    @ExpectPlatform @Deprecated
    public static void addBlockRenderType(RenderType type, Block... blocks) {
        throw new AssertionError();
    }

    @ExpectPlatform @Deprecated
    public static void addFluidRenderType(RenderType type, Fluid... fluids) {
        throw new AssertionError();
    }

    @ExpectPlatform @SafeVarargs @Deprecated
    public static void addItemColor(ItemColor color, Supplier<? extends ItemLike>... items) {
        throw new AssertionError();
    }

    @ExpectPlatform @SafeVarargs @Deprecated
    public static void addBlockColor(BlockColor color, Supplier<? extends Block>... items) {
        throw new AssertionError();
    }

    @ExpectPlatform @Deprecated
    public static <T extends Entity> void addEntityRenderer(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> renderer) {
        throw new AssertionError();
    }

    @ExpectPlatform @Deprecated
    public static <T extends BlockEntity> void addBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> renderer) {
        throw new AssertionError();
    }

    @ExpectPlatform @Deprecated
    public static void addLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        throw new AssertionError();
    }

    public static void registerSpecialModel(ModelResourceLocation model) {
        SPECIAL_MODELS.add(model);
    }

    public static void registerSpecialModels(ModelResourceLocation... models) {
        for (ModelResourceLocation model : models) {
            registerSpecialModel(model);
        }
    }
}