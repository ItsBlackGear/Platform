package com.blackgear.platform.client.forge;

import com.blackgear.platform.client.GameRendering;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameRenderingImpl {
    public static void registerBlockColors(Consumer<GameRendering.BlockColorEvent> listener) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        GameRendering.BlockColorEvent colorEvent = new GameRendering.BlockColorEvent() {
            @Override
            public void register(ItemColor color, ItemLike... items) {
                bus.addListener((RegisterColorHandlersEvent.Item event) -> event.register(color, items));
            }

            @Override
            public void register(BlockColor color, Block... blocks) {
                bus.addListener((RegisterColorHandlersEvent.Block event) -> event.register(color, blocks));
            }
        };

        listener.accept(colorEvent);
    }

    @SuppressWarnings("removal")
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
        Consumer<EntityRenderersEvent.RegisterRenderers> consumer = event -> listener.accept(event::registerEntityRenderer);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }

    public static void registerBlockEntityRenderers(Consumer<GameRendering.BlockEntityRendererEvent> listener) {
        Consumer<EntityRenderersEvent.RegisterRenderers> consumer = event -> listener.accept(event::registerBlockEntityRenderer);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }

    public static void registerModelLayers(Consumer<GameRendering.ModelLayerEvent> listener) {
        Consumer<EntityRenderersEvent.RegisterLayerDefinitions> consumer = event -> listener.accept(event::registerLayerDefinition);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }

    public static void registerSpecialModels(Consumer<GameRendering.SpecialModelEvent> listener) {
        Consumer<ModelEvent.RegisterAdditional> consumer = event -> {
            GameRendering.SpecialModelEvent modelEvent = new GameRendering.SpecialModelEvent() {
                @Override
                public void register(ResourceLocation model) {
                    event.register(model);
                }

                @Override
                public void register(ResourceLocation... models) {
                    Arrays.stream(models).forEach(event::register);
                }
            };
            listener.accept(modelEvent);
        };
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }

    public static void registerSkullRenderers(Consumer<GameRendering.SkullRendererEvent> listener) {
        Consumer<EntityRenderersEvent.CreateSkullModels> consumer = event -> {
            GameRendering.SkullRendererEvent skullEvent = new GameRendering.SkullRendererEvent() {
                @Override
                public void registerSkullModel(SkullBlock.Type type, Function<ModelPart, SkullModelBase> model, ModelLayerLocation layer) {
                    event.registerSkullModel(type, model.apply(event.getEntityModelSet().bakeLayer(layer)));
                }

                @Override
                public void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture) {
                    SkullBlockRenderer.SKIN_BY_TYPE.put(type, texture);
                }
            };
            listener.accept(skullEvent);
        };
        FMLJavaModLoadingContext.get().getModEventBus().addListener(consumer);
    }
}