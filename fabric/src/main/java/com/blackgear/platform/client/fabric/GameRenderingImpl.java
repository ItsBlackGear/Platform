package com.blackgear.platform.client.fabric;

import com.blackgear.platform.client.GameRendering;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.impl.client.model.ModelLoadingRegistryImpl;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameRenderingImpl {
    public static void registerBlockColors(Consumer<GameRendering.BlockColorEvent> listener) {
        listener.accept(new GameRendering.BlockColorEvent() {
            @Override
            public void register(ItemColor color, ItemLike... items) {
                Arrays.stream(items).forEach(item -> ColorProviderRegistry.ITEM.register(color, item));
            }

            @Override
            public void register(BlockColor color, Block... blocks) {
                Arrays.stream(blocks).forEach(block -> ColorProviderRegistry.BLOCK.register(color, block));
            }
        });
    }

    public static void registerBlockRenderers(Consumer<GameRendering.BlockRendererEvent> listener) {
        listener.accept(new GameRendering.BlockRendererEvent() {
            @Override
            public void register(RenderType type, Block... blocks) {
                BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);
            }

            @Override
            public void register(RenderType type, Fluid... fluids) {
                BlockRenderLayerMap.INSTANCE.putFluids(type, fluids);
            }
        });
    }

    public static void registerEntityRenderers(Consumer<GameRendering.EntityRendererEvent> listener) {
        listener.accept(new GameRendering.EntityRendererEvent() {
            @Override
            public <E extends Entity> void register(EntityType<? extends E> type, Function<EntityRenderDispatcher, EntityRenderer<E>> renderer) {
                EntityRendererRegistry.INSTANCE.register(type, (dispatcher, context) -> renderer.apply(dispatcher));
            }
        });
    }

    public static void registerBlockEntityRenderers(Consumer<GameRendering.BlockEntityRendererEvent> listener) {
        listener.accept(BlockEntityRendererRegistry.INSTANCE::register);
    }

    public static void registerSpecialModels(Consumer<GameRendering.SpecialModelEvent> listener) {
        listener.accept(new GameRendering.SpecialModelEvent() {
            @Override
            public void register(ResourceLocation model) {
                ModelLoadingRegistryImpl.INSTANCE.registerModelProvider((manager, loader) -> loader.accept(model));
            }

            @Override
            public void register(ResourceLocation... models) {
                Arrays.stream(models).forEach(this::register);
            }
        });
    }
}