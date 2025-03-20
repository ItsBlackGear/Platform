package com.blackgear.platform.core.events;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.server.level.ServerPlayer;

public interface DatapackSyncEvents {
    Event<DatapackSyncEvents> EVENT = Event.create(DatapackSyncEvents.class);

    void onSync(ServerPlayer player);
}