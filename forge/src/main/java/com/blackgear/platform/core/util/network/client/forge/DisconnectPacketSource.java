package com.blackgear.platform.core.util.network.client.forge;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public interface DisconnectPacketSource {
    Packet<?> createDisconnectPacket(Component message);
}