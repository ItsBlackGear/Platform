package com.blackgear.platform.core.network;

import com.blackgear.platform.core.network.base.NetworkDirection;
import com.blackgear.platform.core.network.base.Packet;
import com.blackgear.platform.core.network.base.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Collection;

public class NetworkChannel {
    private final ResourceLocation channel;

    public NetworkChannel(String modId, int version, String channel) {
        this.channel = new ResourceLocation(modId, channel);
        PacketRegistry.registerChannel(this.channel, version);
    }

    public <T extends Packet<T>> void registerPacket(NetworkDirection direction, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        if (direction == NetworkDirection.C2S) {
            PacketRegistry.registerC2SPacket(this.channel, id, handler, packet);
        } else {
            PacketRegistry.registerS2CPacket(this.channel, id, handler, packet);
        }
    }

    public <T extends Packet<T>> void sendToServer(T packet) {
        PacketRegistry.sendToServer(this.channel, packet);
    }

    public <T extends Packet<T>> void sendToPlayer(T packet, Player player) {
        PacketRegistry.sendToPlayer(this.channel, packet, player);
    }

    public <T extends Packet<T>> void sendToPlayers(T packet, Collection<? extends Player> players) {
        players.forEach(player -> sendToPlayer(packet, player));
    }

    public <T extends Packet<T>> void sendToAllPlayers(T packet, MinecraftServer server) {
        sendToPlayers(packet, server.getPlayerList().getPlayers());
    }

    public <T extends Packet<T>> void sendToPlayersInLevel(T packet, Level level) {
        sendToPlayers(packet, level.players());
    }

    public <T extends Packet<T>> void sendToAllLoadedPlayers(T packet, Level level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        if (level.getChunkSource() instanceof ServerChunkCache cache) {
            cache.chunkMap.getPlayers(chunk.getPos(), false)
                .forEach(player -> sendToPlayer(packet, player));
        }
    }

    public <T extends Packet<T>> void sendToPlayersInRange(T packet, Level level, BlockPos pos, double range) {
        level.players().stream()
            .filter(player -> player.blockPosition().distSqr(pos) <= range)
            .forEach(player -> sendToPlayer(packet, player));
    }
}