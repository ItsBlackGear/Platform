package com.blackgear.platform.client.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.RendererRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    value = Dist.CLIENT,
    bus = Mod.EventBusSubscriber.Bus.MOD
)
public class RendererRegistryImpl {
    private static final Set<Consumer<EntityRenderersEvent.RegisterRenderers>> RENDERERS = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<EntityRenderersEvent.RegisterLayerDefinitions>> LAYER_DEFINITIONS = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<RegisterColorHandlersEvent.Block>> BLOCK_COLORS = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<RegisterColorHandlersEvent.Item>> ITEM_COLORS = ConcurrentHashMap.newKeySet();

    public static void addBlockRenderType(RenderType type, Block... blocks) {
        Arrays.stream(blocks).forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, type));
    }
    
    public static void addFluidRenderType(RenderType type, Fluid... fluids) {
        Arrays.stream(fluids).forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, type));
    }

    @SafeVarargs
    public static void addItemColor(ItemColor color, Supplier<? extends ItemLike>... items) {
        ITEM_COLORS.add(event -> Arrays.stream(items).forEach(item -> event.register(color, item.get())));
    }
    
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        ITEM_COLORS.forEach(consumer -> consumer.accept(event));
    }
    
    @SafeVarargs
    public static void addBlockColor(BlockColor color, Supplier<? extends Block>... items) {
        BLOCK_COLORS.add(event -> Arrays.stream(items).forEach(item -> event.register(color, item.get())));
    }
    
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BLOCK_COLORS.forEach(consumer -> consumer.accept(event));
    }

    public static <T extends Entity> void addEntityRenderer(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> renderer) {
        RENDERERS.add(event -> event.registerEntityRenderer(type.get(), renderer));
    }
    
    public static <T extends BlockEntity> void addBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> renderer) {
        RENDERERS.add(event -> event.registerBlockEntityRenderer(type.get(), renderer));
    }
    
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        RENDERERS.forEach(consumer -> consumer.accept(event));
    }
    
    public static void addLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        LAYER_DEFINITIONS.add(event -> event.registerLayerDefinition(layer, definition));
    }
    
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        LAYER_DEFINITIONS.forEach(consumer -> consumer.accept(event));
    }

    @SubscribeEvent
    public static void registerSpecialModels(ModelEvent.RegisterAdditional event) {
        RendererRegistry.SPECIAL_MODELS.forEach(event::register);
    }

    public static void registerSkullModel(SkullBlock.Type type, Function<ModelPart, SkullModelBase> factory, ModelLayerLocation layerLocation) {
        RendererRegistry.MODEL_BY_SKULL.put(type, new Pair<>(factory, layerLocation));
    }

    public static void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture) {
        RendererRegistry.TEXTURE_BY_SKULL.put(type, texture);
    }

    @SubscribeEvent
    public static void registerSkulls(EntityRenderersEvent.CreateSkullModels event) {
        RendererRegistry.MODEL_BY_SKULL.forEach((type, pair) -> event.registerSkullModel(type, pair.getFirst().apply(event.getEntityModelSet().bakeLayer(pair.getSecond()))));
        SkullBlockRenderer.SKIN_BY_TYPE.putAll(RendererRegistry.TEXTURE_BY_SKULL);
    }
}