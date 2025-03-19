package com.blackgear.platform.core.network;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.network.base.NetworkDirection;
import com.blackgear.platform.core.network.packet.ConfigSyncPacket;
import com.blackgear.platform.core.network.packet.NetworkPacketWrapper;

public class MessageHandler {
    public static final NetworkChannel DEFAULT_CHANNEL = new NetworkChannel(Platform.MOD_ID, 1, "networking");

    public static void bootstrap() {
        DEFAULT_CHANNEL.registerPacket(NetworkDirection.S2C, ConfigSyncPacket.ID, ConfigSyncPacket.HANDLER, ConfigSyncPacket.class);
        DEFAULT_CHANNEL.registerPacket(NetworkDirection.S2C, NetworkPacketWrapper.ID, NetworkPacketWrapper.HANDLER, NetworkPacketWrapper.class);
    }
}