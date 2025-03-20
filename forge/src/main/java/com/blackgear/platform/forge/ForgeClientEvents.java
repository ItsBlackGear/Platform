package com.blackgear.platform.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.event.FogRenderEvents;
import com.blackgear.platform.client.event.HudRenderEvent;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT
)
public class ForgeClientEvents {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderHudEvent(RenderGuiOverlayEvent.Post event) {
        GuiGraphics matrices = event.getGuiGraphics();
        float tickDelta = event.getPartialTick();

        Minecraft minecraft = Minecraft.getInstance();
        HudRenderEvent.RenderContext context = new HudRenderEvent.RenderContext() {};

        if (Minecraft.useFancyGraphics()) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.VIGNETTE, context);
        }

        if (minecraft.gameMode.canHurtPlayer()) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.HEALTH, context);
        }

        if (minecraft.gameMode.hasExperience()) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.EXPERIENCE, context);
        }

        if (minecraft.options.getCameraType().isFirstPerson()) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.FIRST_PERSON, context);
        }

        HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.DEFAULT, context);
    }

    @SubscribeEvent
    public static void renderFogColor(ViewportEvent.ComputeFogColor event) {
        FogRenderEvents.ColorContext context = new FogRenderEvents.ColorContext() {
            private boolean isValid = false;

            @Override
            public Camera getCamera() {
                return event.getCamera();
            }

            @Override
            public float getRed() {
                return event.getRed();
            }

            @Override
            public float getGreen() {
                return event.getGreen();
            }

            @Override
            public float getBlue() {
                return event.getBlue();
            }

            @Override
            public void setRed(float red) {
                event.setRed(red);
            }

            @Override
            public void setGreen(float green) {
                event.setGreen(green);
            }

            @Override
            public void setBlue(float blue) {
                event.setBlue(blue);
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
        FogRenderEvents.FOG_COLOR.invoker().setupColor(event.getRenderer(), context, (float) event.getPartialTick());
    }

    @SubscribeEvent
    public static void renderFog(ViewportEvent.RenderFog event) {
        FogRenderEvents.RenderContext context = new FogRenderEvents.RenderContext() {
            private float start = event.getNearPlaneDistance();
            private float end = event.getFarPlaneDistance();
            private FogShape shape = event.getFogShape();
            private boolean isValid = false;

            @Override
            public Camera camera() {
                return event.getCamera();
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
            public FogShape fogShape() {
                return this.shape;
            }

            @Override
            public FogType fogType() {
                return event.getType();
            }

            @Override
            public FogRenderer.FogMode fogMode() {
                return event.getMode();
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
            public void fogShape(FogShape shape) {
                this.shape = shape;
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
        FogRenderEvents.FOG_RENDERING.invoker().setupRendering(event.getRenderer(), context, event.getNearPlaneDistance(), event.getFarPlaneDistance(), (float) event.getPartialTick());

        if (context.isValid()) {
            event.setNearPlaneDistance(context.fogStart());
            event.setFarPlaneDistance(context.fogEnd());
            event.setFogShape(context.fogShape());
            event.setCanceled(true);
        }
    }
}