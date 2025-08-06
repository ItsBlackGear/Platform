package com.blackgear.platform.core.mixin.config;

import com.blackgear.platform.core.util.config.ConfigTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public class ClientHandshakePacketListenerImplMixin {
    @Shadow @Final private Minecraft minecraft;
    @Unique private boolean hasLoadedConfigs;

    @Inject(method = "handleCustomQuery", at = @At("HEAD"))
    public void handleCustomQuery(ClientboundCustomQueryPacket packet, CallbackInfo ci) {
        if (!this.hasLoadedConfigs && !this.minecraft.hasSingleplayerServer()) {
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
            this.hasLoadedConfigs = true;
        }
    }
}