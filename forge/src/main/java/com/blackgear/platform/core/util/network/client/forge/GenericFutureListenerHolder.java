package com.blackgear.platform.core.util.network.client.forge;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.Nullable;

public record GenericFutureListenerHolder(GenericFutureListener<? extends Future<? super Void>> delegate) implements PacketSendListener {
    @Nullable
    public static GenericFutureListenerHolder create(@Nullable GenericFutureListener<? extends Future<? super Void>> delegate) {
        if (delegate == null) {
            return null;
        }
        
        return new GenericFutureListenerHolder(delegate);
    }
    
    @Override
    public void onSuccess() {
        throw new AssertionError("Should not be called");
    }
    
    @Override
    @Nullable
    public Packet<?> onFailure() {
        throw new AssertionError("Should not be called");
    }
}