package com.blackgear.platform.core.util.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface PacketSender {
    Packet<?> createPacket(ResourceLocation channel, FriendlyByteBuf buffer);
    
    void sendPacket(Packet<?> packet);
    
    void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback);
    
    default void sendPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        Objects.requireNonNull(channel, "Channel name cannot be null");
        Objects.requireNonNull(buffer, "Packet buffer cannot be null");
        
        this.sendPacket(this.createPacket(channel, buffer));
    }
    
    default void sendPacket(ResourceLocation channel, FriendlyByteBuf buffer, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        Objects.requireNonNull(channel, "Channel name cannot be null");
        Objects.requireNonNull(buffer, "Packet buffer cannot be null");
        
        this.sendPacket(this.createPacket(channel, buffer), callback);
    }
}