package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.event.screen.HudInteractions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin<T extends AbstractContainerMenu> {
    @Shadow @Final protected T menu;
    @Shadow @Nullable protected Slot hoveredSlot;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V"
        )
    )
    private void platform$stopHoveringOnRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        for (int i = 0; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.getSlot(i);
            if (slot != this.hoveredSlot) {
                HudInteractions.STOP_HOVERING.invoker().onStopHovering(Minecraft.getInstance(), (AbstractContainerScreen<?>) (Object) this, slot);
            }
        }
    }

    @Inject(
        method = "onClose",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;closeContainer()V",
            shift = At.Shift.AFTER
        )
    )
    private void platform$stopHoveringOnClose(CallbackInfo ci) {
        if (this.hoveredSlot != null) {
            HudInteractions.STOP_HOVERING.invoker().onStopHovering(Minecraft.getInstance(), (AbstractContainerScreen<?>) (Object) this, this.hoveredSlot);
        }
    }

    @Inject(
        method = "slotClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleInventoryMouseClick(IIILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V"
        )
    )
    private void platform$onSlotClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        HudInteractions.SLOT_CLICK.invoker().onMouseClick(Minecraft.getInstance(), (AbstractContainerScreen<?>) (Object) this, slot, type);
    }
}