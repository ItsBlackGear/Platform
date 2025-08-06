package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.screen.HudRendering;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = GameRenderer.class, priority = 1100)
public class GameRendererMixin {
    @Shadow @Final Minecraft minecraft;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    public void renderScreenPre(DeltaTracker tickDelta, boolean renderLevel, CallbackInfo ci, boolean gameLoaded, int mouseX, int mouseY, Window window, Matrix4f matrix, Matrix4fStack matrices, GuiGraphics graphics) {
        if (HudRendering.PRE_RENDERING.invoker().onRender(this.minecraft, this.minecraft.screen, graphics, mouseX, mouseY, tickDelta).isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            shift = At.Shift.AFTER,
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void renderScreenPost(DeltaTracker tickDelta, boolean renderLevel, CallbackInfo ci, boolean gameLoaded, int mouseX, int mouseY, Window window, Matrix4f matrix, Matrix4fStack matrices, GuiGraphics graphics) {
        HudRendering.POST_RENDERING.invoker().onRender(this.minecraft, this.minecraft.screen, graphics, mouseX, mouseY, tickDelta);
    }
}