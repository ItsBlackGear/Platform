package com.blackgear.platform.core.mixin.fabric.client.input;

import com.blackgear.platform.client.event.input.RawInputEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("RETURN"), cancellable = true)
    public void onKeyPress(long handle, int key, int scanCode, int action, int modifiers, CallbackInfo info) {
        if (handle == this.minecraft.getWindow().getWindow()) {
            if (RawInputEvent.ON_KEY_PRESS.invoker().handle(this.minecraft, key, scanCode, action, modifiers).isCancelled()) {
                info.cancel();
            }
        }
    }
}