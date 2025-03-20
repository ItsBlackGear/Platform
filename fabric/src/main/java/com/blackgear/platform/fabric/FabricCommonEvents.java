package com.blackgear.platform.fabric;

import com.blackgear.platform.core.events.DatapackSyncEvents;
import com.blackgear.platform.core.network.listener.ServerListenerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class FabricCommonEvents {
    public static void bootstrap() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerListenerEvents.JOIN.invoker().listener(handler, sender::createPacket, server));
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> DatapackSyncEvents.EVENT.invoker().onSync(player));
    }
}