package com.blackgear.platform.core.networking;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class PayloadDistributor {
    @ExpectPlatform
    public static void sendToServer(CustomPacketPayload packetPayload) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayersInDimension(ServerLevel level, CustomPacketPayload payload) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer excluded, double x, double y, double z, double radius, CustomPacketPayload payload) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToAllPlayers(CustomPacketPayload payload) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos pos, CustomPacketPayload payload) {
        throw new AssertionError();
    }
}