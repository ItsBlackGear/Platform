package com.blackgear.platform.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameRendering {
    @ExpectPlatform
    public static void registerBlockColors(Consumer<BlockColorEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerBlockRenderers(Consumer<BlockRendererEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerEntityRenderers(Consumer<EntityRendererEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerBlockEntityRenderers(Consumer<BlockEntityRendererEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerModelLayers(Consumer<ModelLayerEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerSpecialModels(Consumer<SpecialModelEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerSkullRenderers(Consumer<SkullRendererEvent> listener) {
        throw new AssertionError();
    }

    public interface BlockColorEvent {
        void register(ItemColor color, ItemLike... items);

        void register(BlockColor color, Block... blocks);
    }

    public interface BlockRendererEvent {
        void register(RenderType type, Block... blocks);

        void register(RenderType type, Fluid... fluids);
    }

    public interface EntityRendererEvent {
        <E extends Entity> void register(EntityType<? extends E> type, EntityRendererProvider<E> renderer);
    }

    public interface BlockEntityRendererEvent {
        <E extends BlockEntity> void register(BlockEntityType<? extends E> type, BlockEntityRendererProvider<E> renderer);
    }

    public interface ModelLayerEvent {
        void register(ModelLayerLocation layer, Supplier<LayerDefinition> definition);
    }

    public interface SpecialModelEvent {
        void register(ResourceLocation model);
    }

    public interface SkullRendererEvent {
        void registerSkullModel(SkullBlock.Type type, Function<ModelPart, SkullModelBase> model, ModelLayerLocation layer);

        void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture);
    }
}