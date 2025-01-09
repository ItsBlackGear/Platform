package com.blackgear.platform.fabric;

import com.blackgear.platform.client.event.HudRenderEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class FabricClientEvents {
    public static void bootstrap() {
        renderHudEvent();
    }
    
    private static void renderHudEvent() {
        HudRenderCallback.EVENT.register((stack, tickDelta) -> {
            HudRenderEvent.RenderContext context = new HudRenderEvent.RenderContext() {};
            Minecraft minecraft = Minecraft.getInstance();
            
            if (Minecraft.useFancyGraphics()) {
                HudRenderEvent.RENDER_HUD.invoker().render(stack, tickDelta, HudRenderEvent.ElementType.VIGNETTE, context);
            }
            
            if (!minecraft.options.hideGui && minecraft.gameMode.canHurtPlayer()) {
                HudRenderEvent.RENDER_HUD.invoker().render(stack, tickDelta, HudRenderEvent.ElementType.HEALTH, context);
            }
            
            if (minecraft.gameMode.hasExperience()) {
                HudRenderEvent.RENDER_HUD.invoker().render(stack, tickDelta, HudRenderEvent.ElementType.EXPERIENCE, context);
            }
            
            HudRenderEvent.RENDER_HUD.invoker().render(stack, tickDelta, HudRenderEvent.ElementType.DEFAULT, context);
        });
    }
}