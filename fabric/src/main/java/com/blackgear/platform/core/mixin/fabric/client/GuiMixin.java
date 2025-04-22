package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.HudRenderEvent;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/LayeredDraw;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"
        )
    )
    private void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        HudRenderEvent.RenderContext context = new HudRenderEvent.RenderContext() {
            @Override
            public Window window() {
                return minecraft.getWindow();
            }

            @Override
            public int screenWidth() {
                return guiGraphics.guiWidth();
            }

            @Override
            public int screenHeight() {
                return guiGraphics.guiHeight();
            }
        };
        HudRenderEvent.RENDER_HUD.invoker().render(guiGraphics, deltaTracker.getGameTimeDeltaTicks(), HudRenderEvent.ElementType.FIRST_PERSON, context);
    }
}