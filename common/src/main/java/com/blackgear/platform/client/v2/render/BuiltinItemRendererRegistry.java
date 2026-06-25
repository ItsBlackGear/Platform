package com.blackgear.platform.client.v2.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public abstract class BuiltinItemRendererRegistry {
    @ExpectPlatform
    public static BuiltinItemRendererRegistry getInstance() {
        throw new AssertionError();
    }

    public abstract void register(ItemLike item, Renderer renderer);

    public abstract Renderer get(ItemLike item);

    @FunctionalInterface
    public interface Renderer {
        void render(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int packedLight, int combinedOverlay);
    }
}