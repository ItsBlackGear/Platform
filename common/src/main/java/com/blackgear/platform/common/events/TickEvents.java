package com.blackgear.platform.common.events;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

public interface TickEvents {
    Event<ClientTick> CLIENT_TICK_PRE = Event.create(ClientTick.class);
    Event<ClientTick> CLIENT_TICK_POST = Event.create(ClientTick.class);

    Event<ServerTick> SERVER_TICK_PRE = Event.create(ServerTick.class);
    Event<ServerTick> SERVER_TICK_POST = Event.create(ServerTick.class);

    Event<LevelTick> LEVEL_TICK_PRE = Event.create(LevelTick.class);
    Event<LevelTick> LEVEL_TICK_POST = Event.create(LevelTick.class);

    interface ClientTick {
        void handle();
    }

    interface ServerTick {
        void handle(MinecraftServer server);
    }

    interface LevelTick {
        void handle(Level level);
    }
}