package com.blackgear.platform.fabric;

import com.blackgear.platform.client.event.HudRenderEvent;
import com.blackgear.platform.client.event.screen.HudRendering;
import com.blackgear.platform.client.event.screen.TooltipEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class FabricClientEvents {
    public static void bootstrap() {
        renderHudEvent();
        renderTooltipEvent();
    }

    private static void renderTooltipEvent() {
        ItemTooltipCallback.EVENT.register((stack, flags, components) -> TooltipEvents.ITEM_SETUP.invoker().registerTooltip(stack, components, flags));
        HudRenderCallback.EVENT.register((graphics, tickDelta) -> HudRendering.RENDERING.invoker().onRender(Minecraft.getInstance(), graphics, tickDelta));
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