package com.blackgear.platform.core.util.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.FriendlyByteBuf;

public final class FutureListeners {
    @ExpectPlatform
    public static ChannelFutureListener free(FriendlyByteBuf buffer) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static boolean isLocalChannel(Channel channel) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static <A extends Future<? super Void>, B extends Future<? super Void>> GenericFutureListener<? extends Future<? super Void>> union(
        GenericFutureListener<A> first,
        GenericFutureListener<B> second
    ) {
        throw new AssertionError();
    }
}