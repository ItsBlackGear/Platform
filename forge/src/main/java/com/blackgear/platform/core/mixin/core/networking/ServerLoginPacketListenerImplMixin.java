package com.blackgear.platform.core.mixin.core.networking;

import com.blackgear.platform.core.util.network.client.forge.AbstractNetworkAddon;
import com.blackgear.platform.core.util.network.client.forge.DisconnectPacketSource;
import com.blackgear.platform.core.util.network.client.forge.NetworkHandlerExtensions;
import com.blackgear.platform.core.util.network.client.forge.PacketCallbackListener;
import com.blackgear.platform.core.util.network.server.forge.ServerLoginNetworkAddon;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLoginPacketListenerImpl.class, priority = 998)
public abstract class ServerLoginPacketListenerImplMixin implements NetworkHandlerExtensions, DisconnectPacketSource, PacketCallbackListener {
    @Unique private ServerLoginNetworkAddon addon;
    
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void platform$init(CallbackInfo ci) {
        this.addon = new ServerLoginNetworkAddon((ServerLoginPacketListenerImpl) (Object) this);
    }
    
    @WrapWithCondition(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;handleAcceptedLogin()V"
        )
    )
    private boolean platform$handlePlayerJoin(ServerLoginPacketListenerImpl handler) {
        // Do not accept the player, thereby moving into play stage until all login futures being waited on are completed
        return this.addon.queryTick();
    }
    
    @Inject(
        method = "handleCustomQueryPacket",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$handleCustomPayloadReceivedAsync(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        // Handle queries
        if (this.addon.handle(packet)) {
            ci.cancel();
        }
    }
    
    @WrapOperation(
        method = "handleAcceptedLogin",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/MinecraftServer;getCompressionThreshold()I",
            ordinal = 0
        )
    )
    private int platform$removeLateCompressionPacketSending(MinecraftServer instance, Operation<Integer> original) {
        return -1;
    }
    
    @Inject(
        method = "onDisconnect",
        at = @At("HEAD")
    )
    private void platform$handleDisconnection(Component reason, CallbackInfo ci) {
        this.addon.handleDisconnect();
    }
    
    @Inject(
        method = "handleAcceptedLogin",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;"
        )
    )
    private void platform$handlePlayTransitionNormal(CallbackInfo ci) {
        this.addon.handlePlayTransition();
    }
    
    @Override
    public void sent(Packet<?> packet) {
        if (packet instanceof ClientboundCustomQueryPacket) {
            this.addon.registerOutgoingPacket((ClientboundCustomQueryPacket) packet);
        }
    }
    
    @Override
    public AbstractNetworkAddon<?> getAddon() {
        return this.addon;
    }
    
    @Override
    public Packet<?> createDisconnectPacket(Component message) {
        return new ClientboundLoginDisconnectPacket(message);
    }
    
}