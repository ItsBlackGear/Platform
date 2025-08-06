package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.LocalPlayerEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayerPacketListenerMixin extends ClientCommonPacketListenerImpl {
    @Unique private LocalPlayer oldPlayer;

    protected ClientPlayerPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(
        method = "handleLogin",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Options;setServerRenderDistance(I)V",
            shift = At.Shift.AFTER
        )
    )
    private void platform$handleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        LocalPlayerEvents.ON_LOGIN.invoker().onLogin(this.minecraft.player);
    }

    @Inject(
        method = "handleRespawn",
        at = @At("HEAD")
    )
    private void platform$captureOldPlayer(ClientboundRespawnPacket packet, CallbackInfo ci) {
        this.oldPlayer = this.minecraft.player;
    }

    @Inject(
        method = "handleRespawn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;addEntity(Lnet/minecraft/world/entity/Entity;)V"
        )
    )
    private void platform$handleRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        LocalPlayerEvents.ON_RESPAWN.invoker().onRespawn(this.oldPlayer, this.minecraft.player);
        this.oldPlayer = null;
    }
}