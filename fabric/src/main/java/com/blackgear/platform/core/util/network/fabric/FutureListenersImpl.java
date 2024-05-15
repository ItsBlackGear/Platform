package com.blackgear.platform.core.util.network.fabric;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.networking.v1.FutureListeners;
import net.minecraft.network.FriendlyByteBuf;

public class FutureListenersImpl {
    public static ChannelFutureListener free(FriendlyByteBuf buffer) {
        return FutureListeners.free(buffer);
    }
    
    public static boolean isLocalChannel(Channel channel) {
        return FutureListeners.isLocalChannel(channel);
    }
    
    public static <A extends Future<? super Void>, B extends Future<? super Void>> GenericFutureListener<? extends Future<? super Void>> union(
        GenericFutureListener<A> first,
        GenericFutureListener<B> second
    ) {
        return FutureListeners.union(first, second);
    }
}