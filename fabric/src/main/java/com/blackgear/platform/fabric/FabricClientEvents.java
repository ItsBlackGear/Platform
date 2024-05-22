package com.blackgear.platform.fabric;

import com.blackgear.platform.client.event.HudRenderEvent;
import com.mojang.blaze3d.platform.Window;
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
            HudRenderEvent.RenderContext context = new HudRenderEvent.RenderContext() {
                @Override
                public Window getWindow() {
                    return minecraft.getWindow();
                }
                
                @Override
                public int getScreenWidth() {
                    return minecraft.getWindow().getGuiScaledWidth();
                }
                
                @Override
                public int getScreenHeight() {
                    return minecraft.getWindow().getGuiScaledHeight();
                }
            };
            
            if (Minecraft.useFancyGraphics()) {
                HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.VIGNETTE, context);
            } else if (minecraft.gameMode.canHurtPlayer()) {
                HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.HEALTH, context);
            } else if (minecraft.gameMode.hasExperience()) {
                HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.EXPERIENCE, context);
            } else if (minecraft.options.getCameraType().isFirstPerson()) {
                HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.FIRST_PERSON, context);
            } else {
                HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.DEFAULT, context);
            }
        });
    }
}