package com.blackgear.platform.core.mixin.core.access;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundCustomPayloadPacket.class)
public interface ServerboundCustomPayloadPacketAccessor {
    @Accessor
    ResourceLocation getIdentifier();
    
    @Accessor
    void setIdentifier(ResourceLocation identifier);
    
    @Accessor
    FriendlyByteBuf getData();
    
    @Accessor
    void setData(FriendlyByteBuf data);
}
