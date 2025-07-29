package com.blackgear.platform.core.mixin.fabric.client.screen;

import com.blackgear.platform.client.event.screen.HudRendering;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainScreenMixin extends Screen {
    protected AbstractContainScreenMixin(Component title) {
        super(title);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        HudRendering.RENDER_BACKGROUND.invoker().onRender(this.minecraft, (AbstractContainerScreen<?>) (Object) this, graphics, mouseX, mouseY, tickDelta);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        HudRendering.RENDER_FOREGROUND.invoker().onRender(this.minecraft, (AbstractContainerScreen<?>) (Object) this, graphics, mouseX, mouseY, tickDelta);
    }
}