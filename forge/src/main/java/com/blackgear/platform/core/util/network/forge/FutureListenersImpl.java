package com.blackgear.platform.core.util.network.forge;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public class FutureListenersImpl {
    public static ChannelFutureListener free(FriendlyByteBuf buffer) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return (future) -> {
            if (!isLocalChannel(future.channel())) {
                buffer.release();
            }
        };
    }
    
    public static boolean isLocalChannel(Channel channel) {
        return channel instanceof LocalServerChannel || channel instanceof LocalChannel;
    }
    
    @SuppressWarnings("unchecked")
    public static <A extends Future<? super Void>, B extends Future<? super Void>> GenericFutureListener<? extends Future<? super Void>> union(
        GenericFutureListener<A> first,
        GenericFutureListener<B> second
    ) {
        // Return an empty future listener in the case of both parameters somehow being null
        if (first == null && second == null) {
            return future -> { };
        }
        
        if (first == null) {
            return second;
        }
        
        if (second == null) {
            return first;
        }
        
        return future -> {
            first.operationComplete((A) future);
            second.operationComplete((B) future);
        };
    }
}