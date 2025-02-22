package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.helper.SkullRegistry;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(SkullBlockRenderer.class)
public class SkullBlockRendererMixin {
    @Inject(
        method = "method_3579",
        at = @At("TAIL")
    )
    private static void addSkullModels(HashMap<SkullBlock.Type, SkullModel> map, CallbackInfo ci) {
        map.putAll(SkullRegistry.MODEL_BY_SKULL);
    }

    @Inject(
        method = "method_3580",
        at = @At("TAIL")
    )
    private static void addSkullTextures(HashMap<SkullBlock.Type, ResourceLocation> map, CallbackInfo ci) {
        map.putAll(SkullRegistry.TEXTURE_BY_SKULL);
    }
}
