package com.blackgear.platform.core.util.network.client.forge;

import net.minecraft.network.protocol.Packet;

public interface PacketCallbackListener {
    void sent(Packet<?> packet);
}