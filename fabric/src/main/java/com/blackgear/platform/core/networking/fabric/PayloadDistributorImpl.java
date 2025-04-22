package com.blackgear.platform.core.networking.fabric;

import com.blackgear.platform.core.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PayloadDistributorImpl {
    public static void sendToServer(CustomPacketPayload payload) {
        if (Environment.isClientSide()) {
            ClientPlayNetworking.send(payload);
        }
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToPlayersInDimension(ServerLevel level, CustomPacketPayload payload) {
        PlayerLookup.world(level).forEach(player -> ServerPlayNetworking.send(player, payload));
    }

    public static void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer excluded, double x, double y, double z, double radius, CustomPacketPayload payload) {
        PlayerLookup.around(level, new Vec3(x, y, z), radius).forEach(player -> {
            if (player != excluded) {
                ServerPlayNetworking.send(player, payload);
            }
        });
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        Environment.getCurrentServer().ifPresent(server -> {
            PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, payload));
        });
    }

    public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload) {
        PlayerLookup.tracking(entity).forEach(player -> ServerPlayNetworking.send(player, payload));
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos pos, CustomPacketPayload payload) {
        PlayerLookup.tracking(level, pos).forEach(player -> ServerPlayNetworking.send(player, payload));
    }
}