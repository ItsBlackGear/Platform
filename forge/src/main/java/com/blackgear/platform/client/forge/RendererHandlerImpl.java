package com.blackgear.platform.client.forge;

import com.blackgear.platform.Platform;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.animation.TileEntityRendererAnimation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    value = Dist.CLIENT,
    bus = Mod.EventBusSubscriber.Bus.MOD
)
public class RendererHandlerImpl {
    private static final Set<Consumer<ColorHandlerEvent.Block>> BLOCK_COLORS = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<ColorHandlerEvent.Item>> ITEM_COLORS = ConcurrentHashMap.newKeySet();

    // ========== RenderType Registry ==========

    public static void addBlockRenderType(RenderType type, Block... blocks) {
        Arrays.stream(blocks).forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, type));
    }

    public static void addFluidRenderType(RenderType type, Fluid... fluids) {
        Arrays.stream(fluids).forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, type));
    }

    // ========== Color Registry ==========

    @SafeVarargs
    public static void addItemColor(ItemColor color, Supplier<? extends ItemLike>... items) {
        ITEM_COLORS.add(event -> Arrays.stream(items).forEach(item -> event.getItemColors().register(color, item.get())));
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        ITEM_COLORS.forEach(consumer -> consumer.accept(event));
    }

    @SafeVarargs
    public static void addBlockColor(BlockColor color, Supplier<? extends Block>... items) {
        BLOCK_COLORS.add(event -> Arrays.stream(items).forEach(item -> event.getBlockColors().register(color, item.get())));
    }

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        BLOCK_COLORS.forEach(consumer -> consumer.accept(event));
    }

    // ========== Rendering Registry ==========

    public static <T extends Entity> void addEntityRenderer(Supplier<? extends EntityType<? extends T>> type, Function<EntityRenderDispatcher, EntityRenderer<T>> renderer) {
        RenderingRegistry.registerEntityRenderingHandler(type.get(), renderer::apply);
    }
    
    public static <T extends BlockEntity> void addBlockEntityRenderer(Supplier<BlockEntityType<T>> type, Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>> renderer) {
        ClientRegistry.bindTileEntityRenderer(type.get(), renderer);
    }
}