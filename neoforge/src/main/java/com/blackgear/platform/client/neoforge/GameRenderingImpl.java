package com.blackgear.platform.client.neoforge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.GameRendering;
import com.blackgear.platform.core.util.EventBus;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameRenderingImpl {
    public static void registerBlockColors(Consumer<GameRendering.BlockColorEvent> listener) {
        EventBus.get(EventBus.MOD).addListener((RegisterColorHandlersEvent.Block event) -> {
            listener.accept(new GameRendering.BlockColorEvent() {
                @Override
                public void register(BlockColor color, Block... blocks) {
                    event.register(color, blocks);
                }

                @Override
                public int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tint) {
                    return event.getBlockColors().getColor(state, level, pos, tint);
                }
            });
        });
    }

    public static void registerItemColors(Consumer<GameRendering.ItemColorEvent> listener) {
        EventBus.get(EventBus.MOD).addListener((RegisterColorHandlersEvent.Item event) -> {
            listener.accept(new GameRendering.ItemColorEvent() {
                @Override
                public void register(ItemColor color, ItemLike... items) {
                    event.register(color, items);
                }

                @Override
                public int getColor(ItemStack stack, int tint) {
                    if (stack.getItem() instanceof BlockItem blockItem) {
                        BlockState state = blockItem.getBlock().defaultBlockState();
                        return event.getBlockColors().getColor(state, null, null, tint);
                    }

                    return 0xFFFFFF;
                }
            });
        });
    }

    public static void registerBlockRenderers(Consumer<GameRendering.BlockRendererEvent> listener) {
        listener.accept(new GameRendering.BlockRendererEvent() {
            @Override
            public void register(RenderType type, Block... blocks) {
                for (Block block : blocks) ItemBlockRenderTypes.setRenderLayer(block, type);
            }

            @Override
            public void register(RenderType type, Fluid... fluids) {
                for (Fluid fluid : fluids) ItemBlockRenderTypes.setRenderLayer(fluid, type);
            }
        });
    }

    public static void registerEntityRenderers(Consumer<GameRendering.EntityRendererEvent> listener) {
        EventBus.get(EventBus.MOD).addListener((EntityRenderersEvent.RegisterRenderers event) -> listener.accept(event::registerEntityRenderer));
    }

    public static void registerBlockEntityRenderers(Consumer<GameRendering.BlockEntityRendererEvent> listener) {
        EventBus.get(EventBus.MOD).addListener((EntityRenderersEvent.RegisterRenderers event) -> listener.accept(event::registerBlockEntityRenderer));
    }

    public static void registerModelLayers(Consumer<GameRendering.ModelLayerEvent> listener) {
        EventBus.get(EventBus.MOD).addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> listener.accept(event::registerLayerDefinition));
    }

    public static void registerSpecialModels(Consumer<GameRendering.SpecialModelEvent> listener) {
        EventBus.get(EventBus.MOD).addListener((ModelEvent.RegisterAdditional event) -> {
            listener.accept(new GameRendering.SpecialModelEvent() {
                @Override
                public void register(ResourceLocation model) {
                    event.register(new ModelResourceLocation(wrapModel(model), "standalone"));
                }

                @Override
                public void register(ResourceLocation... models) {
                    for (ResourceLocation model : models) this.register(model);
                }
            });
        });
    }

    public static void registerModelOverrides(Consumer<GameRendering.ModelOverrideEvent> listener) {
        listener.accept(new GameRendering.ModelOverrideEvent() {});

        EventBus.get(EventBus.MOD).addListener((ModelEvent.ModifyBakingResult event) -> {
            Map<ModelResourceLocation, BakedModel> registry = event.getModels();
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : GameRendering.MODEL_OVERRIDES.entrySet()) {
                ModelResourceLocation originalModel = new ModelResourceLocation(entry.getKey(), "inventory");
                ModelResourceLocation overrideModel = new ModelResourceLocation(wrapModel(entry.getValue()), "standalone");
                BakedModel overridenModel = registry.get(overrideModel);

                if (overridenModel != null) {
                    registry.put(originalModel, overridenModel);
                } else {
                    Platform.LOGGER.error("Failed to find custom model: {}", overrideModel);
                }
            }
        });
        EventBus.get(EventBus.MOD).addListener((ModelEvent.RegisterAdditional event) -> {
            for (ResourceLocation override : GameRendering.MODEL_OVERRIDES.values()) {
                event.register(new ModelResourceLocation(wrapModel(override), "standalone"));
            }
        });
    }

    private static ResourceLocation wrapModel(ResourceLocation model) {
        return ResourceLocation.fromNamespaceAndPath(model.getNamespace(), "item/" + model.getPath());
    }

    public static void registerSkullRenderers(Consumer<GameRendering.SkullRendererEvent> listener) {
        EventBus.get(EventBus.MOD).addListener((EntityRenderersEvent.CreateSkullModels event) -> {
            listener.accept(new GameRendering.SkullRendererEvent() {
                @Override
                public void registerSkullModel(SkullBlock.Type type, Function<ModelPart, SkullModelBase> model, ModelLayerLocation layer) {
                    event.registerSkullModel(type, model.apply(event.getEntityModelSet().bakeLayer(layer)));
                }

                @Override
                public void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture) {
                    SkullBlockRenderer.SKIN_BY_TYPE.put(type, texture);
                }
            });
        });
    }

    public static void registerParticleFactories(Consumer<GameRendering.ParticleFactoryEvent> listener) {
        EventBus.get(EventBus.MOD).addListener((RegisterParticleProvidersEvent event) -> {
            listener.accept(new GameRendering.ParticleFactoryEvent() {
                @Override
                public <T extends ParticleOptions, P extends ParticleType<T>> void register(Supplier<P> type, ParticleProvider<T> provider) {
                    event.registerSpecial(type.get(), provider);
                }

                @Override
                public <T extends ParticleOptions, P extends ParticleType<T>> void register(Supplier<P> type, Factory<T> factory) {
                    event.registerSpriteSet(type.get(), factory::create);
                }
            });
        });
    }
}