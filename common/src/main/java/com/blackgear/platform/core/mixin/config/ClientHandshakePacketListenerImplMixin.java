package com.blackgear.platform.core.mixin.config;

import com.blackgear.platform.core.util.config.ConfigLoader;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public class ClientHandshakePacketListenerImplMixin {
    @Shadow @Final private Connection connection;
    
    @Inject(
        method = "handleGameProfile",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/Connection;setProtocol(Lnet/minecraft/network/ConnectionProtocol;)V",
            shift = At.Shift.AFTER
        )
    )
    public void handleGameProfile(ClientboundGameProfilePacket clientboundGameProfilePacket, CallbackInfo callbackInfo) {
        ConfigLoader.handleClientLoginSuccess(this.connection);
    }
}
