package com.blackgear.platform.core.network;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.network.base.NetworkDirection;
import com.blackgear.platform.core.network.packet.ConfigSyncPacket;

public class MessageHandler {
    public static final NetworkChannel DEFAULT_CHANNEL = new NetworkChannel(Platform.MOD_ID, 1, "networking");

    public static void bootstrap() {
        DEFAULT_CHANNEL.registerPacket(NetworkDirection.TO_CLIENT, ConfigSyncPacket.ID, ConfigSyncPacket.HANDLER, ConfigSyncPacket.class);
    }
}