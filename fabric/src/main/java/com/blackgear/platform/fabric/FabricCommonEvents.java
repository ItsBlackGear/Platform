package com.blackgear.platform.fabric;

import com.blackgear.platform.core.network.listener.ServerListenerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class FabricCommonEvents {
    public static void bootstrap() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerListenerEvents.JOIN.invoker().listener(handler, sender::createPacket, server));
    }
}