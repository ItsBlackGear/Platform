package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.LocalPlayerEvents;
import com.blackgear.platform.client.event.screen.HudRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Nullable public LocalPlayer player;

    @Inject(
        method = "clearClientLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/GameNarrator;clear()V"
        )
    )
    private void platform$onLogout(Screen screen, CallbackInfo ci) {
        LocalPlayerEvents.ON_LOGOUT.invoker().onLogout(this.player);
    }

    @Inject(
        method = "setScreen",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void platform$onScreenOpen(Screen screen, CallbackInfo ci) {
        HudRendering.OPEN_CONTAINER.invoker().onOpen((Minecraft)(Object)this, screen);
    }

    @Inject(
        method = "setScreen",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;removed()V"
        )
    )
    private void platform$onScreenClose(Screen screen, CallbackInfo ci) {
        Minecraft minecraft = (Minecraft)(Object)this;
        HudRendering.CLOSE_CONTAINER.invoker().onClose(minecraft, minecraft.screen);
    }
}