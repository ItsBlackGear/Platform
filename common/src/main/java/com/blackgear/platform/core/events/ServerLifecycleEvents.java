package com.blackgear.platform.core.events;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.server.MinecraftServer;

public class ServerLifecycleEvents {
    public static final Event<Starting> STARTING = Event.create(Starting.class);
    public static final Event<Started> STARTED = Event.create(Started.class);
    public static final Event<Stopping> STOPPING = Event.create(Stopping.class);
    public static final Event<Stopped> STOPPED = Event.create(Stopped.class);
    
    @FunctionalInterface
    public interface Starting {
        void starting(MinecraftServer server);
    }
    
    @FunctionalInterface
    public interface Started {
        void started(MinecraftServer server);
    }
    
    @FunctionalInterface
    public interface Stopping {
        void stopping(MinecraftServer server);
    }
    
    @FunctionalInterface
    public interface Stopped {
        void stopped(MinecraftServer server);
    }
}