package com.blackgear.platform.common.data.entity;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.entity.SyncedDataKey.SyncMode;
import com.blackgear.platform.common.events.EntityTrackingEvents;
import com.blackgear.platform.common.events.TickEvents;
import com.blackgear.platform.core.network.packets.ClientboundEntityDataPacket;
import com.blackgear.platform.core.network.packets.ClientboundEntityDataPacket.Entry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class SyncedEntityDataContainer {
    private static final Set<Entity> DIRTY_ENTITIES = ConcurrentHashMap.newKeySet();
    
    private final Map<SyncedDataKey<?>, Object> values = new ConcurrentHashMap<>();
    private final Set<SyncedDataKey<?>> dirty = ConcurrentHashMap.newKeySet();
    private final Entity entity;
    
    public static void bootstrap() {
        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            if (player instanceof ServerPlayer sp) SyncedDataHolder.get(entity).syncTrackingDataTo(sp);
        });
        
        TickEvents.SERVER_TICK_POST.register(server -> SyncedEntityDataContainer.flushAll());
    }
    
    public SyncedEntityDataContainer(Entity entity) {
        this.entity = entity;
    }
    
    public <T> void set(SyncedDataKey<T> key, T value) {
        Object prev = this.values.put(key, value);
        if (!Objects.equals(prev, value) && !this.entity.level().isClientSide() && key.mode() != SyncMode.NONE) {
            this.dirty.add(key);
            DIRTY_ENTITIES.add(this.entity);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(SyncedDataKey<T> key) {
        return (T) this.values.getOrDefault(key, key.getFallback());
    }
    
    public void flush() {
        if (this.dirty.isEmpty()) return;
        
        List<Entry<?>> tracking = new ArrayList<>();
        List<Entry<?>> self = new ArrayList<>();
        
        for (SyncedDataKey<?> key : this.dirty) {
            Object value = this.values.get(key);
            
            if (key.mode().isTracking()) tracking.add(asEntry(key, value));
            
            if (key.mode().isSelf()) self.add(asEntry(key, value));
        }
        
        if (!tracking.isEmpty()) {
            Platform.NETWORKING.sendToPlayersTrackingEntity(new ClientboundEntityDataPacket(this.entity.getId(), tracking), this.entity);
        }
        
        if (!self.isEmpty() && this.entity instanceof ServerPlayer player) {
            Platform.NETWORKING.sendToPlayer(new ClientboundEntityDataPacket(this.entity.getId(), self), player);
        }
        
        this.dirty.clear();
    }
    
    public static void flushAll() {
        if (DIRTY_ENTITIES.isEmpty()) return;
        
        for (Entity entity : DIRTY_ENTITIES) {
            SyncedDataHolder.get(entity).flush();
        }
        
        DIRTY_ENTITIES.clear();
    }
    
    public void syncTrackingDataTo(ServerPlayer player) {
        List<Entry<?>> entries = collectEntries(SyncMode::isTracking);
        if (!entries.isEmpty()) Platform.NETWORKING.sendToPlayer(new ClientboundEntityDataPacket(this.entity.getId(), entries), player);
    }
    
    public void syncSelfDataTo(ServerPlayer player) {
        List<Entry<?>> entries = collectEntries(SyncMode::isSelf);
        if (!entries.isEmpty()) Platform.NETWORKING.sendToPlayer(new ClientboundEntityDataPacket(this.entity.getId(), entries), player);
    }
    
    private List<Entry<?>> collectEntries(Predicate<SyncMode> filter) {
        List<Entry<?>> entries = new ArrayList<>();
        this.values.forEach((key, value) -> {
            if (filter.test(key.mode())) entries.add(asEntry(key, value));
        });
        return entries;
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Entry<T> asEntry(SyncedDataKey<?> key, Object value) {
        return new Entry<>((SyncedDataKey<T>) key, (T) value);
    }
}