package com.blackgear.platform.core.util.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
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

public class PlayerLookup {
    @ExpectPlatform
    public static Collection<ServerPlayer> all(MinecraftServer server) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Collection<ServerPlayer> level(ServerLevel level) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Collection<ServerPlayer> tracking(ServerLevel level, ChunkPos pos) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Collection<ServerPlayer> tracking(Entity entity) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Collection<ServerPlayer> tracking(BlockEntity blockEntity) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Collection<ServerPlayer> tracking(ServerLevel level, BlockPos pos) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Collection<ServerPlayer> around(ServerLevel level, Vec3 pos, double radius) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Collection<ServerPlayer> around(ServerLevel level, Vec3i pos, double radius) {
        throw new AssertionError();
    }
}