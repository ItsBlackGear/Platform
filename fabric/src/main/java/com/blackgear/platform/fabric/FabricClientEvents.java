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
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
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
        });
    }
}