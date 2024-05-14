package com.blackgear.platform.core.mixin.core.networking;

import com.blackgear.platform.core.util.network.ChannelInfoHolder;
import com.blackgear.platform.core.util.network.DisconnectPacketSource;
import com.blackgear.platform.core.util.network.NetworkHandlerExtensions;
import com.blackgear.platform.core.util.network.PacketCallbackListener;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = Connection.class, priority = 1001)
public abstract class ConnectionMixin implements ChannelInfoHolder {
    @Shadow private PacketListener packetListener;
    
    @Shadow public abstract void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback);
    @Shadow public abstract void disconnect(Component disconnectReason);
    
    @Unique private Collection<ResourceLocation> playChannels;
    
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void platform$init(PacketFlow side, CallbackInfo ci) {
        this.playChannels = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }
    
    @WrapOperation(
        method = "exceptionCaught",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V"
        )
    )
    private void platform$resendOnExceptionCaught(Connection instance, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener, Operation<Void> original) {
        PacketListener handler = this.packetListener;
        
        if (handler instanceof DisconnectPacketSource) {
            this.send(((DisconnectPacketSource) handler).createDisconnectPacket(new TranslatableComponent("disconnect.genericReason")), listener);
        } else {
            this.disconnect(new TranslatableComponent("disconnect.genericReason")); // Don't send packet if we cannot send proper packets
        }
    }
    
    @Inject(
        method = "sendPacket",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/network/Connection;sentPackets:I"
        )
    )
    private void platform$sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci) {
        if (this.packetListener instanceof PacketCallbackListener) {
            ((PacketCallbackListener) this.packetListener).sent(packet);
        }
    }
    
    @Inject(
        method = "channelInactive",
        at = @At("HEAD")
    )
    private void platform$channelInactive(ChannelHandlerContext channelHandlerContext, CallbackInfo ci) throws Exception {
        if (packetListener instanceof NetworkHandlerExtensions) { // not the case for client/server query
            ((NetworkHandlerExtensions) packetListener).getAddon().handleDisconnect();
        }
    }
    
    @Override
    public Collection<ResourceLocation> getPendingChannelNames() {
        return this.playChannels;
    }
}