package com.blackgear.platform.core.util.network.forge;

import com.blackgear.platform.core.mixin.core.access.ChunkMapAccessor;
import com.blackgear.platform.core.mixin.core.access.TrackedEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerLookupImpl {
    public static Collection<ServerPlayer> all(MinecraftServer server) {
        Objects.requireNonNull(server, "server cannot be null");
        
        if (server.getPlayerList() != null) {
            return Collections.unmodifiableCollection(server.getPlayerList().getPlayers());
        }
        
        return Collections.emptyList();
    }
    
    public static Collection<ServerPlayer> level(ServerLevel level) {
        Objects.requireNonNull(level, "level cannot be null");
        
        return Collections.unmodifiableCollection(level.players());
    }
    
    public static Collection<ServerPlayer> tracking(ServerLevel level, ChunkPos pos) {
        Objects.requireNonNull(level, "level cannot be null");
        Objects.requireNonNull(pos, "pos cannot be null");
        
        return level.getChunkSource().chunkMap.getPlayers(pos, false).collect(Collectors.toList());
    }
    
    public static Collection<ServerPlayer> tracking(Entity entity) {
        Objects.requireNonNull(entity, "entity cannot be null");
        ChunkSource manager = entity.level.getChunkSource();
        
        if (manager instanceof ServerChunkCache) {
            ChunkMap map = ((ServerChunkCache) manager).chunkMap;
            TrackedEntityAccessor tracker = ((ChunkMapAccessor) map).getEntityMap().get(entity.getId());
            
            if (tracker != null) {
                return Collections.unmodifiableCollection(tracker.getSeenBy());
            }
            
            return Collections.emptySet();
        }
        
        throw new IllegalArgumentException("Only supported on server levels!");
    }
    
    public static Collection<ServerPlayer> tracking(BlockEntity blockEntity) {
        Objects.requireNonNull(blockEntity, "blockEntity cannot be null");
        
        return tracking((ServerLevel) blockEntity.getLevel(), blockEntity.getBlockPos());
    }
    
    public static Collection<ServerPlayer> tracking(ServerLevel level, BlockPos pos) {
        Objects.requireNonNull(level, "level cannot be null");
        Objects.requireNonNull(pos, "pos cannot be null");
        
        return tracking(level, new ChunkPos(pos));
    }
    
    public static Collection<ServerPlayer> around(ServerLevel level, Vec3 pos, double radius) {
        double range = radius * radius;
        
        return level(level)
            .stream()
            .filter(player -> player.distanceToSqr(pos) <= range)
            .collect(Collectors.toList());
    }
    
    public static Collection<ServerPlayer> around(ServerLevel level, Vec3i pos, double radius) {
        double range = radius * radius;
        
        return level(level)
            .stream()
            .filter(player -> player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= range)
            .collect(Collectors.toList());
    }
}