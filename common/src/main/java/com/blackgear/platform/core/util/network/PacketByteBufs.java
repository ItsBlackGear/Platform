package com.blackgear.platform.core.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

@SuppressWarnings("VulnerableCodeUsages")
public class PacketByteBufs {
    private static final FriendlyByteBuf EMPTY_PACKET_BYTE_BUF = new FriendlyByteBuf(Unpooled.EMPTY_BUFFER);
    
    public static FriendlyByteBuf empty() {
        return EMPTY_PACKET_BYTE_BUF;
    }
    
    public static FriendlyByteBuf create() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }
    
    public static FriendlyByteBuf readBytes(ByteBuf buffer, int length) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.readBytes(length));
    }
    
    public static FriendlyByteBuf readSlice(ByteBuf buffer, int length) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.readSlice(length));
    }
    
    public static FriendlyByteBuf readRetainedSlice(ByteBuf buffer, int length) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.readRetainedSlice(length));
    }
    
    public static FriendlyByteBuf copy(ByteBuf buffer) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.copy());
    }
    
    public static FriendlyByteBuf copy(ByteBuf buffer, int index, int length) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.copy(index, length));
    }
    
    public static FriendlyByteBuf slice(ByteBuf buffer) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.slice());
    }
    
    public static FriendlyByteBuf retainedSlice(ByteBuf buffer) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.retainedSlice());
    }
    
    public static FriendlyByteBuf slice(ByteBuf buffer, int index, int length) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.slice(index, length));
    }
    
    public static FriendlyByteBuf retainedSlice(ByteBuf buffer, int index, int length) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.retainedSlice(index, length));
    }
    
    public static FriendlyByteBuf duplicate(ByteBuf buffer) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.duplicate());
    }
    
    public static FriendlyByteBuf retainedDuplicate(ByteBuf buffer) {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        return new FriendlyByteBuf(buffer.retainedDuplicate());
    }
}