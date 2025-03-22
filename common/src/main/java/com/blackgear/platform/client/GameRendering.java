package com.blackgear.platform.client;

import com.blackgear.platform.client.renderer.model.geom.ModelLayerLocation;
import com.blackgear.platform.client.renderer.model.geom.ModelLayers;
import com.blackgear.platform.client.renderer.model.geom.builder.LayerDefinition;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameRendering {
    public static Map<ModelLayerLocation, LayerDefinition> MODEL_LAYERS = new ConcurrentHashMap<>();
    public static final Map<Item, ModelResourceLocation> HAND_HELD_MODELS = new ConcurrentHashMap<>();
    public static final Map<SkullBlock.Type, ResourceLocation> TEXTURE_BY_SKULL = new ConcurrentHashMap<>();
    public static final Map<SkullBlock.Type, SkullModel> MODEL_BY_SKULL = new ConcurrentHashMap<>();

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

    public static void registerModelLayers(Consumer<ModelLayerEvent> listener) {
        ModelLayerEvent event = (layer, provider) -> {
            if (MODEL_LAYERS.putIfAbsent(layer, provider) != null) {
                throw new IllegalArgumentException(String.format("Model layer %s is already registered!", layer));
            }

            ModelLayers.ALL_MODELS.add(layer);
        };
        listener.accept(event);
    }

    @ExpectPlatform
    public static void registerSpecialModels(Consumer<SpecialModelEvent> listener) {
        throw new AssertionError();
    }

    public static void registerHandHeldModels(Consumer<HandHeldModelEvent> listener) {
        listener.accept(HAND_HELD_MODELS::put);
    }

    public static void registerSkullRenderers(Consumer<GameRendering.SkullRendererEvent> listener) {
        listener.accept(new GameRendering.SkullRendererEvent() {
            @Override
            public void registerSkullModel(SkullBlock.Type type, SkullModel model) {
                MODEL_BY_SKULL.put(type, model);
            }

            @Override
            public void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture) {
                TEXTURE_BY_SKULL.put(type, texture);
            }
        });
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
        <E extends Entity> void register(EntityType<? extends E> type, Function<EntityRenderDispatcher, EntityRenderer<E>> renderer);
    }

    public interface BlockEntityRendererEvent {
        <E extends BlockEntity> void register(BlockEntityType<E> type, Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super E>> renderer);
    }

    public interface ModelLayerEvent {
        void register(ModelLayerLocation layer, LayerDefinition provider);
    }

    public interface SpecialModelEvent {
        void register(ResourceLocation model);
        void register(ResourceLocation... models);
    }

    public interface HandHeldModelEvent {
        void register(Item item, ModelResourceLocation model);
    }

    public interface SkullRendererEvent {
        void registerSkullModel(SkullBlock.Type type, SkullModel model);

        void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture);
    }
}