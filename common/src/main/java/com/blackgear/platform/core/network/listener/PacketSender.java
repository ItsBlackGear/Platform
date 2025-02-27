package com.blackgear.platform.core.network.listener;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface PacketSender {
    void sendPacket(ResourceLocation channel, FriendlyByteBuf data);
}