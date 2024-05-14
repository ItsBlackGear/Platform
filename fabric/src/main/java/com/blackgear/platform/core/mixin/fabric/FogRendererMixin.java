package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.client.FogRenderingHandler.FogColorContext;
import com.blackgear.platform.client.FogRenderingHandler.FogRenderingContext;
import com.blackgear.platform.client.fabric.FogRenderingHandlerImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;
    
    @Inject(
        method = "setupColor",
        at = @At("HEAD"),
        cancellable = true)
    private static void platform$setupFogColor(
        Camera activeRenderInfo,
        float partialTicks,
        ClientLevel level,
        int renderDistanceChunks,
        float bossColorModifier,
        CallbackInfo ci
    ) {
        FogColorContext context = FogRenderingHandlerImpl.COLOR.invoker()
            .setupColor(new FogColorContext(activeRenderInfo, fogRed, fogGreen, fogBlue));
        
        if (context != null) {
            fogRed = context.getRed();
            fogGreen = context.getGreen();
            fogBlue = context.getBlue();
            ci.cancel();
        }
    }
    
    @Inject(
        method = "setupFog",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void platform$setupFogDensity(
        Camera camera,
        FogRenderer.FogMode fogMode,
        float farPlaneDistance,
        boolean nearFog,
        float partialTicks,
        CallbackInfo ci
    ) {
        FogRenderingContext rendering = FogRenderingHandlerImpl.RENDERING.invoker()
            .setupRendering(new FogRenderingContext(camera, fogMode, farPlaneDistance));
        if (rendering != null) {
            ci.cancel();
        }
    }
}