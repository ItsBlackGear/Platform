package com.blackgear.platform.core.util.network;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class GlobalReceiverRegistry<H> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<ResourceLocation, H> handlers;
    private final Set<AbstractNetworkAddon<H>> trackedAddons = new HashSet<>();
    
    public GlobalReceiverRegistry() {
        this(new HashMap<>());
    }
    
    public GlobalReceiverRegistry(Map<ResourceLocation, H> handlers) {
        this.handlers = handlers;
    }
    
    @Nullable
    public H getHandler(ResourceLocation channel) {
        Lock lock = this.lock.readLock();
        lock.lock();
        
        try {
            return this.handlers.get(channel);
        } finally {
            lock.unlock();
        }
    }
    
    public boolean registerGlobalReceiver(ResourceLocation channel, H handler) {
        Objects.requireNonNull(channel, "Channel name cannot be null");
        Objects.requireNonNull(handler, "Packet handler cannot be null");
        
        if (Networking.isReservedPlayChannel(channel)) {
            throw new IllegalArgumentException(String.format("Cannot register handler for reserved channel with name \"%s\"", channel));
        }
        
        Lock lock = this.lock.writeLock();
        lock.lock();
        
        try {
            final boolean result = this.handlers.putIfAbsent(channel, handler) == null;
            if (result) {
                this.handleRegistration(channel, handler);
            }
            
            return result;
        } finally {
            lock.unlock();
        }
    }
    
    public H unregisterGlobalReceiver(ResourceLocation channel) {
        Objects.requireNonNull(channel, "Channel name cannot be null");
        
        if (Networking.isReservedPlayChannel(channel)) {
            throw new IllegalArgumentException(String.format("Cannot register handler for reserved channel with name \"%s\"", channel));
        }
        
        Lock lock = this.lock.writeLock();
        lock.lock();
        
        try {
            final H result = this.handlers.remove(channel);
            if (result != null) {
                this.handleUnregistration(channel);
            }
            
            return result;
        } finally {
            lock.unlock();
        }
    }
    
    public Map<ResourceLocation, H> getHandlers() {
        Lock lock = this.lock.writeLock();
        lock.lock();
        
        try {
            return new HashMap<>(this.handlers);
        } finally {
            lock.unlock();
        }
    }
    
    public Set<ResourceLocation> getChannels() {
        Lock lock = this.lock.readLock();
        lock.lock();
        
        try {
            return new HashSet<>(this.handlers.keySet());
        } finally {
            lock.unlock();
        }
    }
    
    public void startSession(AbstractNetworkAddon<H> addon) {
        Lock lock = this.lock.writeLock();
        lock.lock();
        
        try {
            this.trackedAddons.add(addon);
        } finally {
            lock.unlock();
        }
    }
    
    public void endSession(AbstractNetworkAddon<H> addon) {
        Lock lock = this.lock.writeLock();
        lock.lock();
        
        try {
            this.trackedAddons.remove(addon);
        } finally {
            lock.unlock();
        }
    }
    
    private void handleRegistration(ResourceLocation channel, H handler) {
        Lock lock = this.lock.writeLock();
        lock.lock();
        
        try {
            for (AbstractNetworkAddon<H> addon : this.trackedAddons) {
                addon.registerChannel(channel, handler);
            }
        } finally {
            lock.unlock();
        }
    }
    
    private void handleUnregistration(ResourceLocation channel) {
        Lock lock = this.lock.writeLock();
        lock.lock();
        
        try {
            for (AbstractNetworkAddon<H> addon : this.trackedAddons) {
                addon.unregisterChannel(channel);
            }
        } finally {
            lock.unlock();
        }
    }
}