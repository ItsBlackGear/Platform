package com.blackgear.platform.core.util.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class FabricPacketSender implements net.fabricmc.fabric.api.networking.v1.PacketSender {
    final PacketSender sender;
    
    public FabricPacketSender(PacketSender sender) {
        this.sender = sender;
    }
    
    @Override
    public Packet<?> createPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        return this.sender.createPacket(channel, buffer);
    }
    
    @Override
    public void sendPacket(Packet<?> packet) {
        this.sender.sendPacket(packet);
    }
    
    @Override
    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        this.sender.sendPacket(packet, callback);
    }
    
    @Override
    public void sendPacket(Packet<?> packet, @Nullable PacketSendListener callback) {
        this.sender.sendPacket(packet, callback);
    }
}