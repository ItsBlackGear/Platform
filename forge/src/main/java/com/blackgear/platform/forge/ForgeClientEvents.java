package com.blackgear.platform.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.event.FogRenderEvents;
import com.blackgear.platform.client.event.HudRenderEvent;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
    public static void renderHudEvent(RenderGameOverlayEvent.Post event) {
        PoseStack matrices = event.getMatrixStack();
        float tickDelta = event.getPartialTicks();
        
        HudRenderEvent.RenderContext context = new HudRenderEvent.RenderContext() {};
        
        if (event.getType() == RenderGameOverlayEvent.ElementType.VIGNETTE) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.VIGNETTE, context);
        }
        
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.HEALTH, context);
        }
        
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.EXPERIENCE, context);
        }
        
        if (event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.FIRST_PERSON, context);
        }
        
        HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.DEFAULT, context);
    }
    
    @SubscribeEvent
    public static void renderFogColor(EntityViewRenderEvent.FogColors event) {
        FogRenderEvents.ColorContext context = new FogRenderEvents.ColorContext() {
            private boolean isValid = false;
            
            @Override
            public Camera getCamera() {
                return event.getInfo();
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
        FogRenderEvents.FOG_COLOR.invoker().setupColor(event.getRenderer(), context, (float) event.getRenderPartialTicks());
    }
    
    @SubscribeEvent
    public static void renderFog(EntityViewRenderEvent.FogDensity event) {
        FogRenderEvents.RenderContext context = new ForgeRenderContext(event, event.getDensity());
        FogRenderEvents.FOG_RENDERING.invoker().setupRendering(event.getRenderer(), context, 0.0F);
        
        if (context.isValid() && context.fogDensity() >= 0) {
            event.setDensity(context.fogDensity());
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void renderFog(EntityViewRenderEvent.RenderFogEvent event) {
        FogRenderEvents.RenderContext context = new ForgeRenderContext(event, -1);
        FogRenderEvents.FOG_RENDERING.invoker().setupRendering(event.getRenderer(), context, event.getFarPlaneDistance());
        
        if (context.isValid()) {
            RenderSystem.fogStart(context.fogStart());
            RenderSystem.fogEnd(context.fogEnd());
        }
    }
    
    public static class ForgeRenderContext implements FogRenderEvents.RenderContext {
        private final EntityViewRenderEvent event;
        private float start;
        private float end;
        private float density;
        private boolean isValid = false;
        
        public ForgeRenderContext(EntityViewRenderEvent event, float density) {
            this.event = event;
            this.density = density;
        }
        
        @Override
        public Camera camera() {
            return event.getInfo();
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
        public void fogMode(GlStateManager.FogMode mode) {
            if (this.event instanceof EntityViewRenderEvent.RenderFogEvent) {
                FogRenderEvents.RenderContext.super.fogMode(mode);
            }
        }
        
        @Override
        public void setupNvFogDistance() {
            if (this.event instanceof EntityViewRenderEvent.RenderFogEvent) {
                FogRenderEvents.RenderContext.super.setupNvFogDistance();
            }
        }
        
        @Override
        public boolean isValid() {
            return this.isValid;
        }
        
        @Override
        public void build() {
            this.isValid = true;
        }
    }
}