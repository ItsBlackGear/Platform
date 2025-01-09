package com.blackgear.platform.core.mixin.core.access;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundCustomQueryPacket.class)
public interface ServerboundCustomQueryPacketAccessor {
    @Accessor
    FriendlyByteBuf getData();
    
    @Accessor
    int getTransactionId();
}
