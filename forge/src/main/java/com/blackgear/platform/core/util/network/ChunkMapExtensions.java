package com.blackgear.platform.core.util.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public interface ChunkMapExtensions {
    Collection<ServerPlayer> getTrackingPlayers(Entity entity);
}