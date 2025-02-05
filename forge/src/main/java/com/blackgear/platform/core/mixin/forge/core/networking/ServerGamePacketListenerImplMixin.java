package com.blackgear.platform.core.mixin.forge.core.networking;

import com.blackgear.platform.core.util.network.client.forge.AbstractNetworkAddon;
import com.blackgear.platform.core.util.network.client.forge.DisconnectPacketSource;
import com.blackgear.platform.core.util.network.client.forge.NetworkHandlerExtensions;
import com.blackgear.platform.core.util.network.server.forge.ServerPlayNetworkAddon;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 998)
public class ServerGamePacketListenerImplMixin implements NetworkHandlerExtensions, DisconnectPacketSource {
    @Shadow @Final private MinecraftServer server;
    @Unique private ServerPlayNetworkAddon addon;
    
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void platform$init(CallbackInfo ci) {
        this.addon = new ServerPlayNetworkAddon((ServerGamePacketListenerImpl) (Object) this, this.server);
        // A bit of a hack, but it allows the field above to be set in case someone registers handlers during INIT event which refers to said field
        this.addon.lateInit();
    }
    
    @Inject(
        method = "handleCustomPayload",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$handleCustomPayloadReceivedAsync(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (this.addon.handle(packet)) {
            ci.cancel();
        }
    }
    
    @Inject(
        method = "onDisconnect",
        at = @At("HEAD")
    )
    private void platform$handleDisconnect(Component reason, CallbackInfo ci) {
        this.addon.handleDisconnect();
    }
    
    
    @Override
    public AbstractNetworkAddon<?> getAddon() {
        return this.addon;
    }
    
    @Override
    public Packet<?> createDisconnectPacket(Component message) {
        return new ClientboundDisconnectPacket(message);
    }
}