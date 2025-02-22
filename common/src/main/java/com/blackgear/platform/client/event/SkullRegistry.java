package com.blackgear.platform.client.event;

import com.blackgear.platform.client.RendererRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class SkullRegistry {
    public static final Map<SkullBlock.Type, ResourceLocation> TEXTURE_BY_SKULL = new ConcurrentHashMap<>();
    public static final Map<SkullBlock.Type, Pair<Function<ModelPart, SkullModelBase>, ModelLayerLocation>> MODEL_BY_SKULL = new ConcurrentHashMap<>();
    public static final List<Supplier<Block>> SKULLS = new ArrayList<>();

    public static void registerSkullModel(SkullBlock.Type type, Function<ModelPart, SkullModelBase> model, ModelLayerLocation layer) {
        RendererRegistry.MODEL_BY_SKULL.put(type, new Pair<>(model, layer));
    }

    public static void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture) {
        RendererRegistry.TEXTURE_BY_SKULL.put(type, texture);
    }

    public static void registerSkullBlocks(List<Supplier<Block>> blocks) {
        SKULLS.addAll(blocks);
    }

    @SafeVarargs
    public static void registerSkullBlocks(Supplier<Block>... blocks) {
        registerSkullBlocks(List.of(blocks));
    }
}