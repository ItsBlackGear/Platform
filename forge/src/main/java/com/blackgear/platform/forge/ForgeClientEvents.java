package com.blackgear.platform.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.event.HudRenderEvent;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
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
        
        HudRenderEvent.RenderContext context = new HudRenderEvent.RenderContext() {
            @Override
            public Window getWindow() {
                return event.getWindow();
            }
            
            @Override
            public int getScreenWidth() {
                return event.getWindow().getGuiScaledWidth();
            }
            
            @Override
            public int getScreenHeight() {
                return event.getWindow().getGuiScaledHeight();
            }
        };
        
        if (event.getType() == RenderGameOverlayEvent.ElementType.VIGNETTE) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.VIGNETTE, context);
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.HEALTH, context);
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.EXPERIENCE, context);
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.FIRST_PERSON, context);
        } else {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.DEFAULT, context);
        }
    }
}