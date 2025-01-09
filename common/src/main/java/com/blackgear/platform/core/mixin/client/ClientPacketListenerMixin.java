package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.common.entity.resource.Packetable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow
    private ClientLevel level;
    
    @Inject(
        method = "handleAddEntity",
        at = @At("TAIL")
    )
    private void vb$handleAddEntity(ClientboundAddEntityPacket packet, CallbackInfo ci) {
        EntityType<?> type = packet.getType();
        Entity entity = type.create(this.level);
        
        if (entity instanceof Packetable) {
            ((Packetable) entity).recreateFromPacket(entity, packet);
            int id = packet.getId();
            this.level.putNonPlayerEntity(id, entity);
        }
    }
}