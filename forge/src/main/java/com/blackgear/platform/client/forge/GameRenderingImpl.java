package com.blackgear.platform.client.forge;

import com.blackgear.platform.client.GameRendering;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameRenderingImpl {
    public static void registerBlockColors(Consumer<GameRendering.BlockColorEvent> listener) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        listener.accept(new GameRendering.BlockColorEvent() {
            @Override
            public void register(ItemColor color, ItemLike... items) {
                bus.addListener((ColorHandlerEvent.Item event) -> event.getItemColors().register(color, items));
            }

            @Override
            public void register(BlockColor color, Block... blocks) {
                bus.addListener((ColorHandlerEvent.Block event) -> event.getBlockColors().register(color, blocks));
            }
        });
    }

    public static void registerBlockRenderers(Consumer<GameRendering.BlockRendererEvent> listener) {
        listener.accept(new GameRendering.BlockRendererEvent() {
            @Override
            public void register(RenderType type, Block... blocks) {
                Arrays.stream(blocks).forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, type));
            }

            @Override
            public void register(RenderType type, Fluid... fluids) {
                Arrays.stream(fluids).forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, type));
            }
        });
    }

    public static void registerEntityRenderers(Consumer<GameRendering.EntityRendererEvent> listener) {
        listener.accept(new GameRendering.EntityRendererEvent() {
            @Override
            public <E extends Entity> void register(EntityType<? extends E> type, Function<EntityRenderDispatcher, EntityRenderer<E>> renderer) {
                RenderingRegistry.registerEntityRenderingHandler(type, renderer::apply);
            }
        });
    }

    public static void registerBlockEntityRenderers(Consumer<GameRendering.BlockEntityRendererEvent> listener) {
        listener.accept(ClientRegistry::bindTileEntityRenderer);
    }

    public static void registerSpecialModels(Consumer<GameRendering.SpecialModelEvent> listener) {
        Consumer<ModelRegistryEvent> exporter = event -> listener.accept(new GameRendering.SpecialModelEvent() {
            @Override
            public void register(ResourceLocation model) {
                ModelLoader.addSpecialModel(model);
            }

            @Override
            public void register(ResourceLocation... models) {
                Arrays.stream(models).forEach(this::register);
            }
        });
        FMLJavaModLoadingContext.get().getModEventBus().addListener(exporter);
    }
}