package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.screen.TooltipEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Inject(method = "renderTooltipInternal", at = @At("HEAD"), cancellable = true)
    private void platform$onRenderTooltipInternal(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner tooltipPositioner, CallbackInfo ci) {
        if (!components.isEmpty()) {
            if (TooltipEvents.RENDER_TOOLTIP.invoker().onRendering((GuiGraphics) (Object) this, components, mouseX, mouseY).isCancelled()) {
                ci.cancel();
            }
        }
    }
}