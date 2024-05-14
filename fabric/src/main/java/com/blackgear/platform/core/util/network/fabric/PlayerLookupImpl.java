package com.blackgear.platform.core.util.network.fabric;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class PlayerLookupImpl {
    public static Collection<ServerPlayer> all(MinecraftServer server) {
        return PlayerLookup.all(server);
    }
    
    public static Collection<ServerPlayer> level(ServerLevel level) {
        return PlayerLookup.world(level);
    }
    
    public static Collection<ServerPlayer> tracking(ServerLevel level, ChunkPos pos) {
        return PlayerLookup.tracking(level, pos);
    }
    
    public static Collection<ServerPlayer> tracking(Entity entity) {
        return PlayerLookup.tracking(entity);
    }
    
    public static Collection<ServerPlayer> tracking(BlockEntity blockEntity) {
        return PlayerLookup.tracking(blockEntity);
    }
    
    public static Collection<ServerPlayer> tracking(ServerLevel level, BlockPos pos) {
        return PlayerLookup.tracking(level, pos);
    }
    
    public static Collection<ServerPlayer> around(ServerLevel level, Vec3 pos, double radius) {
        return PlayerLookup.around(level, pos, radius);
    }
    
    public static Collection<ServerPlayer> around(ServerLevel level, Vec3i pos, double radius) {
        return PlayerLookup.around(level, pos, radius);
    }
}