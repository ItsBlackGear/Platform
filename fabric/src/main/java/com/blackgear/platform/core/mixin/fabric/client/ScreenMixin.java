package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.screen.HudRendering;
import com.blackgear.platform.client.event.screen.api.ScreenAccess;
import com.blackgear.platform.client.event.screen.api.ScreenAccessImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Unique private ScreenAccessImpl access;

    @Unique
    private ScreenAccess screenAccess() {
        if (access == null) {
            this.access = new ScreenAccessImpl((Screen) (Object) this);
        }

        this.access.setScreen((Screen) (Object) this);
        return this.access;
    }

    @Inject(
        method = "init(Lnet/minecraft/client/Minecraft;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;init()V"
        ),
        cancellable = true
    )
    private void platform$onScreenPreInitialize(Minecraft minecraft, int width, int height, CallbackInfo ci) {
        if (HudRendering.PRE_INITIALIZE.invoker().onInitialize(minecraft, (Screen) (Object) this, this.screenAccess()).isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(
        method = "init(Lnet/minecraft/client/Minecraft;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;init()V",
            shift = At.Shift.AFTER
        )
    )
    private void platform$onScreenPostInitialize(Minecraft minecraft, int width, int height, CallbackInfo ci) {
        HudRendering.POST_INITIALIZE.invoker().onInitialize(minecraft, (Screen) (Object) this, this.screenAccess());
    }

    @Inject(
        method = "rebuildWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;init()V"
        ),
        cancellable = true
    )
    private void platform$onScreenPreInitializeWidgets(CallbackInfo ci) {
        if (HudRendering.PRE_INITIALIZE.invoker().onInitialize(Minecraft.getInstance(), (Screen) (Object) this, this.screenAccess()).isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(
        method = "rebuildWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;init()V",
            shift = At.Shift.AFTER
        )
    )
    private void platform$onScreenPostInitializeWidgets(CallbackInfo ci) {
        HudRendering.POST_INITIALIZE.invoker().onInitialize(Minecraft.getInstance(), (Screen) (Object) this, this.screenAccess());
    }
}