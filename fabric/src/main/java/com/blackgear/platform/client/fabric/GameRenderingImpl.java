package com.blackgear.platform.client.fabric;

import com.blackgear.platform.client.GameRendering;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.client.model.ModelLoadingRegistryImpl;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameRenderingImpl {
    public static final Map<SkullBlock.Type, ResourceLocation> TEXTURE_BY_SKULL = new ConcurrentHashMap<>();
    public static final Map<SkullBlock.Type, Pair<Function<ModelPart, SkullModelBase>, ModelLayerLocation>> MODEL_BY_SKULL = new ConcurrentHashMap<>();

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

    public static void registerBlockEntityRenderers(Consumer<GameRendering.BlockEntityRendererEvent> listener) {
        listener.accept(BlockEntityRendererRegistry::register);
    }

    public static void registerEntityRenderers(Consumer<GameRendering.EntityRendererEvent> listener) {
        listener.accept(EntityRendererRegistry::register);
    }

    public static void registerModelLayers(Consumer<GameRendering.ModelLayerEvent> listener) {
        listener.accept((layer, definition) -> EntityModelLayerRegistry.registerModelLayer(layer, definition::get));
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void registerSpecialModels(Consumer<GameRendering.SpecialModelEvent> listener) {
        new ModelLoadingRegistryImpl().registerModelProvider((manager, loader) -> listener.accept(loader::accept));
    }

    public static void registerSkullRenderers(Consumer<GameRendering.SkullRendererEvent> listener) {
        GameRendering.SkullRendererEvent event = new GameRendering.SkullRendererEvent() {
            @Override
            public void registerSkullModel(SkullBlock.Type type, Function<ModelPart, SkullModelBase> model, ModelLayerLocation layer) {
                MODEL_BY_SKULL.put(type, new Pair<>(model, layer));
            }

            @Override
            public void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture) {
                TEXTURE_BY_SKULL.put(type, texture);
            }
        };
        listener.accept(event);
    }
}