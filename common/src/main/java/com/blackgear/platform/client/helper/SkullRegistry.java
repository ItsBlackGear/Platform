package com.blackgear.platform.client.helper;

import net.minecraft.client.model.SkullModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SkullRegistry {
    public static final Map<SkullBlock.Type, ResourceLocation> TEXTURE_BY_SKULL = new ConcurrentHashMap<>();
    public static final Map<SkullBlock.Type, SkullModel> MODEL_BY_SKULL = new ConcurrentHashMap<>();
    public static final List<Supplier<Block>> SKULLS = new ArrayList<>();

    public static void registerSkullModel(SkullBlock.Type type, SkullModel model) {
        MODEL_BY_SKULL.put(type, model);
    }

    public static void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture) {
        TEXTURE_BY_SKULL.put(type, texture);
    }

    public static void registerSkullBlocks(List<Supplier<Block>> blocks) {
        SKULLS.addAll(blocks);
    }

    @SafeVarargs
    public static void registerSkullBlocks(Supplier<Block>... block) {
        SKULLS.addAll(Arrays.asList(block));
    }
}