package com.blackgear.platform.client;

import com.blackgear.platform.client.renderer.model.geom.ModelLayerLocation;
import com.blackgear.platform.client.renderer.model.geom.ModelLayers;
import com.blackgear.platform.client.renderer.model.geom.builder.LayerDefinition;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class RendererRegistry {
    public static final Map<ModelLayerLocation, ModelLayerProvider> MODEL_PROVIDERS = new HashMap<>();
    
    @ExpectPlatform
    public static void addBlockRenderType(RenderType type, Block... blocks) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addFluidRenderType(RenderType type, Fluid... fluids) {
        throw new AssertionError();
    }

    @ExpectPlatform @SafeVarargs
    public static void addItemColor(ItemColor color, Supplier<? extends ItemLike>... items) {
        throw new AssertionError();
    }

    @ExpectPlatform @SafeVarargs
    public static void addBlockColor(BlockColor color, Supplier<? extends Block>... items) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> void addEntityRenderer(Supplier<? extends EntityType<? extends T>> type, Function<EntityRenderDispatcher, EntityRenderer<T>> renderer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> void addBlockEntityRenderer(Supplier<BlockEntityType<T>> type, Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>> renderer) {
        throw new AssertionError();
    }
    
    public static void registerModelLayer(ModelLayerLocation model, ModelLayerProvider provider) {
        if (MODEL_PROVIDERS.putIfAbsent(model, provider) != null) {
            throw new IllegalArgumentException(String.format("Model layer %s is already registered!", model));
        }
        
        ModelLayers.ALL_MODELS.add(model);
    }
    
    @ExpectPlatform
    public static void registerSpecialModel(ModelResourceLocation model) {
        throw new AssertionError();
    }
    
    public static void registerSpecialModels(ModelResourceLocation... models) {
        for (ModelResourceLocation model : models) {
            registerSpecialModel(model);
        }
    }
    
    public interface ModelLayerProvider {
        LayerDefinition createLayerDefinition();
    }
}