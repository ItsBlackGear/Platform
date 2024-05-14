package com.blackgear.platform.core.util.network;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityTrackingEvents {
    public static final Event<StartTracking> START_TRACKING = EventFactory.create(StartTracking.class);
    
    public static final Event<StopTracking> STOP_TRACKING = EventFactory.create(StopTracking.class);
    
    @FunctionalInterface
    public interface StartTracking {
        void onStartTracking(Entity target, ServerPlayer player);
    }
    
    @FunctionalInterface
    public interface StopTracking {
        void onStopTracking(Entity target, ServerPlayer player);
    }
}