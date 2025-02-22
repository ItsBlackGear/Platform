package com.blackgear.platform.core.network;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.network.base.NetworkDirection;
import com.blackgear.platform.core.network.packet.ConfigSyncPacket;
import com.blackgear.platform.core.network.packet.PacketWrapper;

public class MessageHandler {
    public static final NetworkChannel DEFAULT_CHANNEL = new NetworkChannel(Platform.MOD_ID, "networking");

    public static void bootstrap() {
        DEFAULT_CHANNEL.registerPacket(NetworkDirection.C2S, ConfigSyncPacket.ID, ConfigSyncPacket.HANDLER, ConfigSyncPacket.class);
        DEFAULT_CHANNEL.registerPacket(NetworkDirection.S2C, PacketWrapper.ID, PacketWrapper.HANDLER, PacketWrapper.class);
    }
}