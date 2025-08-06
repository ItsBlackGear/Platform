package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.FogRendering;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;

    @ModifyArgs(
        method = "setupColor",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V",
            remap = false
        )
    )
    private static void platform$fogColors(
        Args args,
        Camera camera,
        float tickDelta,
        ClientLevel level,
        int farPlaneDistance,
        float skyDarkness
    ) {
        FogRendering.ColorData data = new FogRendering.ColorData(camera, fogRed, fogGreen, fogBlue);
        FogRendering.FOG_COLOR.invoker().setColor(data, tickDelta);
        fogRed = data.getRed();
        fogGreen = data.getGreen();
        fogBlue = data.getBlue();
    }

    @Inject(
        method = "setupFog",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void platform$fogDensity(
        Camera camera,
        FogRenderer.FogMode mode,
        float farPlaneDistance,
        boolean nearFog,
        float tickDelta,
        CallbackInfo ci
    ) {
        float density = FogRendering.FOG_DENSITY.invoker().setDensity(camera, 0.1f);
        if (density != 0.1f) {
            RenderSystem.setShaderFogStart(-8.0F);
            RenderSystem.setShaderFogEnd(density * 0.5F);
            ci.cancel();
        }
    }

    @Inject(
        method = "setupFog",
        at = @At("TAIL")
    )
    private static void platform$fogRendering(
        Camera camera,
        FogRenderer.FogMode mode,
        float farPlaneDistance,
        boolean nearFog,
        float tickDelta,
        CallbackInfo ci,
        @Local FogType type,
        @Local FogRenderer.FogData fogData
    ) {
        FogRendering.FogData data = new FogRendering.FogData(fogData.start, fogData.end, fogData.shape);
        if (FogRendering.FOG_RENDER.invoker().onFogRender(mode, type, camera, tickDelta, farPlaneDistance, fogData.start, fogData.end, fogData.shape, data).isCancelled()) {
            RenderSystem.setShaderFogStart(data.getNearPlaneDistance());
            RenderSystem.setShaderFogEnd(data.getFarPlaneDistance());
            RenderSystem.setShaderFogShape(data.getShape());
        }
    }
}