package com.blackgear.platform.fabric;

import com.blackgear.platform.common.events.CommandRegistrar;
import com.blackgear.platform.common.events.EntityEvents;
import com.blackgear.platform.common.events.TickEvents;
import com.blackgear.platform.core.events.DatapackSyncEvents;
import com.blackgear.platform.core.network.listener.ServerListenerEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class FabricCommonEvents {
    public static void bootstrap() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrar.EVENT.invoker()::register);

        ServerEntityEvents.ENTITY_LOAD.register(EntityEvents.ON_JOIN.invoker()::handle);
        ServerEntityEvents.ENTITY_UNLOAD.register(EntityEvents.ON_LEAVE.invoker()::handle);

        ServerTickEvents.START_SERVER_TICK.register(TickEvents.SERVER_TICK_PRE.invoker()::handle);
        ServerTickEvents.END_SERVER_TICK.register(TickEvents.SERVER_TICK_POST.invoker()::handle);
        ServerTickEvents.START_WORLD_TICK.register(TickEvents.LEVEL_TICK_PRE.invoker()::handle);
        ServerTickEvents.END_WORLD_TICK.register(TickEvents.LEVEL_TICK_POST.invoker()::handle);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerListenerEvents.JOIN.invoker().listener(handler, server));
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> DatapackSyncEvents.EVENT.invoker().onSync(player));
    }
}