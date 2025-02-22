package com.blackgear.platform.client;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class RendererRegistry {
    public static final Set<ModelResourceLocation> SPECIAL_MODELS = ConcurrentHashMap.newKeySet();
    public static final Map<SkullBlock.Type, ResourceLocation> TEXTURE_BY_SKULL = new ConcurrentHashMap<>();
    public static final Map<SkullBlock.Type, Pair<Function<ModelPart, SkullModelBase>, ModelLayerLocation>> MODEL_BY_SKULL = new ConcurrentHashMap<>();

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
    public static <T extends Entity> void addEntityRenderer(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> renderer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> void addBlockEntityRenderer(Supplier<? extends BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> renderer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerSkullModel(SkullBlock.Type type, Function<ModelPart, SkullModelBase> factory, ModelLayerLocation layerLocation) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture) {
        throw new AssertionError();
    }

    public static void registerSpecialModel(ModelResourceLocation model) {
        SPECIAL_MODELS.add(model);
    }

    public static void registerSpecialModels(ModelResourceLocation... models) {
        for (ModelResourceLocation model : models) {
            registerSpecialModel(model);
        }
    }
}