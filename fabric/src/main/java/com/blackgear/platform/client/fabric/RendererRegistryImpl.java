package com.blackgear.platform.client.fabric;

import com.blackgear.platform.client.RendererRegistry;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public class RendererRegistryImpl {
    public static void addBlockRenderType(RenderType type, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);
    }

    public static void addFluidRenderType(RenderType type, Fluid... fluids) {
        BlockRenderLayerMap.INSTANCE.putFluids(type, fluids);
    }

    @SafeVarargs
    public static void addItemColor(ItemColor color, Supplier<? extends ItemLike>... items) {
        Arrays.stream(items).forEach(item -> ColorProviderRegistry.ITEM.register(color, item.get()));
    }

    @SafeVarargs
    public static void addBlockColor(BlockColor color, Supplier<? extends Block>... items) {
        Arrays.stream(items).forEach(block -> ColorProviderRegistry.BLOCK.register(color, block.get()));
    }

    public static <T extends Entity> void addEntityRenderer(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> renderer) {
        EntityRendererRegistry.register(type.get(), renderer);
    }
    
    @SuppressWarnings("UnstableApiUsage")
    public static <T extends BlockEntity> void addBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> renderer) {
        BlockEntityRendererRegistryImpl.register(type.get(), renderer);
    }
    
    public static void addLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        EntityModelLayerRegistry.registerModelLayer(layer, definition::get);
    }

    public static void registerSkullModel(SkullBlock.Type type, Function<ModelPart, SkullModelBase> factory, ModelLayerLocation layerLocation) {
        RendererRegistry.MODEL_BY_SKULL.put(type, new Pair<>(factory, layerLocation));
    }

    public static void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture) {
        RendererRegistry.TEXTURE_BY_SKULL.put(type, texture);
    }
}