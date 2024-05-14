package com.blackgear.platform.core.events;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public class ServerLifecycleEvents {
    public static final Event<Starting> STARTING = EventFactory.create(Starting.class);
    
    public static final Event<Started> STARTED = EventFactory.create(Started.class);
    
    public static final Event<Stopping> STOPPING = EventFactory.create(Stopping.class);
    
    public static final Event<Stopped> STOPPED = EventFactory.create(Stopped.class);
    
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