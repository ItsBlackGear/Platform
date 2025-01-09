package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.FogRenderEvents;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
        cancellable = true
    )
    private static void platform$setupFogColor(
        Camera camera,
        float tickDelta,
        ClientLevel level,
        int renderDistanceChunks,
        float bossColorModifier,
        CallbackInfo ci
    ) {
        FogRenderEvents.ColorContext context = new FogRenderEvents.ColorContext() {
            public boolean isValid = false;
            
            @Override
            public Camera getCamera() {
                return camera;
            }
            
            @Override
            public float getRed() {
                return fogRed;
            }
            
            @Override
            public float getGreen() {
                return fogGreen;
            }
            
            @Override
            public float getBlue() {
                return fogBlue;
            }
            
            @Override
            public void setRed(float red) {
                fogRed = red;
            }
            
            @Override
            public void setGreen(float green) {
                fogGreen = green;
            }
            
            @Override
            public void setBlue(float blue) {
                fogBlue = blue;
            }
            
            @Override
            public boolean isValid() {
                return this.isValid;
            }
            
            @Override
            public void build() {
                this.isValid = true;
            }
        };
        
        FogRenderEvents.FOG_COLOR.invoker().setupColor(Minecraft.getInstance().gameRenderer, context, tickDelta);
        
        if (context.isValid()) {
            ci.cancel();
        }
    }
    
    @Inject(
        method = "setupFog",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void platform$setupFogRendering(
        Camera camera,
        FogRenderer.FogMode mode,
        float farPlaneDistance,
        boolean nearFog,
        CallbackInfo ci
    ) {
        FogRenderEvents.RenderContext context = new FogRenderEvents.RenderContext() {
            private float start;
            private float end;
            private float density = -1;
            private boolean isValid = false;
            
            @Override
            public Camera camera() {
                return camera;
            }
            
            @Override
            public float fogStart() {
                return this.start;
            }
            
            @Override
            public float fogEnd() {
                return this.end;
            }
            
            @Override
            public float fogDensity() {
                return this.density;
            }
            
            @Override
            public void fogStart(float start) {
                this.start = start;
            }
            
            @Override
            public void fogEnd(float end) {
                this.end = end;
            }
            
            @Override
            public void fogDensity(float density) {
                this.density = density;
            }
            
            @Override
            public boolean isValid() {
                return this.isValid;
            }
            
            @Override
            public void build() {
                this.isValid = true;
            }
        };
        FogRenderEvents.FOG_RENDERING.invoker().setupRendering(Minecraft.getInstance().gameRenderer, context, farPlaneDistance);
        
        if (context.isValid()) {
            RenderSystem.fogStart(context.fogStart());
            RenderSystem.fogEnd(context.fogEnd());
            
            if (context.fogDensity() >= 0) {
                RenderSystem.fogDensity(context.fogDensity());
            }
            
            ci.cancel();
        }
    }
}