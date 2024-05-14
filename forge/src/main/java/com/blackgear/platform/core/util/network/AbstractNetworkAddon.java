package com.blackgear.platform.core.util.network;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractNetworkAddon<H> {
    protected final GlobalReceiverRegistry<H> receiver;
    public static final Logger LOGGER = LogManager.getLogger();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<ResourceLocation, H> handlers = new HashMap<>();
    private final AtomicBoolean disconnected = new AtomicBoolean();
    
    protected AbstractNetworkAddon(GlobalReceiverRegistry<H> receiver) {
        this.receiver = receiver;
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
    
    public boolean registerChannel(ResourceLocation channel, H handler) {
        Objects.requireNonNull(
            channel,
            "Channel name cannot be null"
        );
        Objects.requireNonNull(
            handler,
            "Packet handler cannot be null"
        );
        
        if (this.isReservedChannel(channel)) {
            throw new IllegalArgumentException(String.format("Cannot register handler for reserved channel with name \"%s\"", channel));
        }
        
        Lock lock = this.lock.writeLock();
        lock.lock();
        
        try {
            final boolean result = this.handlers.putIfAbsent(channel, handler) == null;
            if (result) {
                this.handleRegistration(channel);
            }
            
            return result;
        } finally {
            lock.unlock();
        }
    }
    
    public H unregisterChannel(ResourceLocation channel) {
        Objects.requireNonNull(
            channel,
            "Channel name cannot be null"
        );
        
        if (this.isReservedChannel(channel)) {
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
    
    public Set<ResourceLocation> getSendableChannels() {
        Lock lock = this.lock.readLock();
        lock.lock();
        
        try {
            return new HashSet<>(this.handlers.keySet());
        } finally {
            lock.unlock();
        }
    }
    
    protected abstract void handleRegistration(ResourceLocation channel);
    
    protected abstract void handleUnregistration(ResourceLocation channel);
    
    public final void handleDisconnect() {
        if (this.disconnected.compareAndSet(false, true)) {
            this.invokeDisconnectEvent();
        }
    }
    
    protected abstract void invokeDisconnectEvent();
    
    protected abstract boolean isReservedChannel(ResourceLocation channel);
}