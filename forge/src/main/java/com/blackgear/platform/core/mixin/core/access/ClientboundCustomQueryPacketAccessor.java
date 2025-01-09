package com.blackgear.platform.core.mixin.core.access;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientboundCustomQueryPacket.class)
public interface ClientboundCustomQueryPacketAccessor {
    @Accessor
    int getTransactionId();
    
    @Accessor
    void setTransactionId(int transactionId);
    
    @Accessor
    ResourceLocation getIdentifier();
    
    @Accessor
    void setIdentifier(ResourceLocation identifier);
    
    @Accessor
    FriendlyByteBuf getData();
    
    @Accessor
    void setData(FriendlyByteBuf data);
}
