package com.blackgear.platform.common.entity.resource;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;

public interface Packetable {
    default void recreateFromPacket(Entity self, ClientboundAddEntityPacket packet) {
        int id = packet.getId();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        self.setPacketCoordinates(x, y, z);
        self.moveTo(x, y, z);
        self.xRot = (float) (packet.getxRot() * 360) / 256.0F;
        self.yRot = (float) (packet.getyRot() * 360) / 256.0F;
        self.setId(id);
        self.setUUID(packet.getUUID());
    }
}