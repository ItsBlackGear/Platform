package com.blackgear.platform.core.mixin.fabric.client.input;

import com.blackgear.platform.client.event.input.RawInputEvent;
import com.blackgear.platform.client.event.screen.HudInteractions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "onScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z",
            ordinal = 0
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onMouseScrollPre(long handle, double xOffset, double yOffset, CallbackInfo ci, double deltaY, double x, double y) {
        if (!ci.isCancelled()) {
            if (HudInteractions.SCROLLING_PRE.invoker().onScrolling(this.minecraft, this.minecraft.screen, x, y, deltaY).isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z",
            ordinal = 0,
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onMouseScrollPost(long handle, double xOffset, double yOffset, CallbackInfo ci, double amountY, double x, double y) {
        if (!ci.isCancelled()) {
            HudInteractions.SCROLLING_POST.invoker().onScrolling(this.minecraft, this.minecraft.screen, x, y, amountY);
        }
    }

    @Inject(method = "onScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z",
            ordinal = 0
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onRawMouseScrolled(long windowPointer, double xOffset, double yOffset, CallbackInfo ci, double deltaY, int i) {
        if (!ci.isCancelled()) {
            if (RawInputEvent.ON_MOUSE_SCROLL.invoker().handle(this.minecraft, deltaY).isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = {"lambda$onPress$0", "method_1611"}, at = @At("HEAD"), cancellable = true, remap = false)
    private static void onMouseClickPre(boolean[] processed, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
        if (!ci.isCancelled()) {
            if (HudInteractions.CLICKING_PRE.invoker().onClicking(Minecraft.getInstance(), screen, mouseX, mouseY, button).isCancelled()) {
                processed[0] = true;
                ci.cancel();
            }
        }
    }

    @Inject(method = {"lambda$onPress$0", "method_1611"}, at = @At("RETURN"), cancellable = true, remap = false)
    private static void onMouseClickPost(boolean[] processed, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
        if (!ci.isCancelled() && !processed[0]) {
            if (HudInteractions.CLICKING_POST.invoker().onClicking(Minecraft.getInstance(), screen, mouseX, mouseY, button).isCancelled()) {
                processed[0] = true;
                ci.cancel();
            }
        }
    }

    @Inject(method = {"lambda$onPress$1", "method_1605"}, at = @At("HEAD"), cancellable = true, remap = false)
    private static void onMouseReleasePre(boolean[] processed, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
        if (!ci.isCancelled()) {
            if (HudInteractions.RELEASING_PRE.invoker().onReleasing(Minecraft.getInstance(), screen, mouseX, mouseY, button).isCancelled()) {
                processed[0] = true;
                ci.cancel();
            }
        }
    }

    @Inject(method = {"lambda$onPress$1", "method_1605"}, at = @At("RETURN"), cancellable = true, remap = false)
    private static void onMouseReleasePost(boolean[] processed, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
        if (!ci.isCancelled() && !processed[0]) {
            if (HudInteractions.RELEASING_POST.invoker().onReleasing(Minecraft.getInstance(), screen, mouseX, mouseY, button).isCancelled()) {
                processed[0] = true;
                ci.cancel();
            }
        }
    }
}