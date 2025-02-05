package com.blackgear.platform.core.mixin.forge.core.networking;

import com.blackgear.platform.core.util.network.client.forge.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = Connection.class, priority = 1001)
public abstract class ConnectionMixin implements ChannelInfoHolder {
    @Shadow private PacketListener packetListener;
    
    @Shadow public abstract void disconnect(Component disconnectReason);
    
    @Shadow public abstract void send(Packet<?> packet, @Nullable PacketSendListener sendListener);
    
    @Unique private Collection<ResourceLocation> playChannels;
    
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void platform$init(PacketFlow side, CallbackInfo ci) {
        this.playChannels = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }
    
    @Redirect(
        method = "exceptionCaught",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"
        )
    )
    private void platform$resendOnExceptionCaught(Connection instance, Packet<?> packet, PacketSendListener sendListener) {
        PacketListener handler = this.packetListener;
        Component disconnectMessage = Component.translatable("disconnect.genericReason");
        
        if (handler instanceof DisconnectPacketSource) {
            this.send(((DisconnectPacketSource) handler).createDisconnectPacket(disconnectMessage), sendListener);
        } else {
            this.disconnect(disconnectMessage); // Don't send packet if we cannot send proper packets
        }
    }
    
    @Inject(
        method = "sendPacket",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/network/Connection;sentPackets:I"
        )
    )
    private void platform$sendPacket(Packet<?> packet, PacketSendListener sendListener, CallbackInfo ci) {
        if (this.packetListener instanceof PacketCallbackListener) {
            ((PacketCallbackListener) this.packetListener).sent(packet);
        }
    }
    
    @Inject(
        method = "channelInactive",
        at = @At("HEAD"),
        remap = false
    )
    private void platform$channelInactive(ChannelHandlerContext channelHandlerContext, CallbackInfo ci) {
        if (packetListener instanceof NetworkHandlerExtensions) { // not the case for client/server query
            ((NetworkHandlerExtensions) packetListener).getAddon().handleDisconnect();
        }
    }
    
    @Inject(
        method = "doSendPacket",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lio/netty/channel/Channel;writeAndFlush(Ljava/lang/Object;)Lio/netty/channel/ChannelFuture;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void platform$sendInternal(
        Packet<?> packet,
        PacketSendListener listener,
        ConnectionProtocol newProtocol,
        ConnectionProtocol currentProtocol,
        CallbackInfo ci,
        ChannelFuture future
    ) {
        if (listener instanceof GenericFutureListenerHolder holder) {
            future.addListener(holder.delegate());
            future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            ci.cancel();
        }
    }
    
    @Override
    public Collection<ResourceLocation> getPendingChannelNames() {
        return this.playChannels;
    }
}