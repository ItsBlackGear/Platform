package com.blackgear.platform.core.network.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.network.base.Packet;
import com.blackgear.platform.core.network.base.PacketHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PacketRegistryImpl {
    public static void registerChannel(ResourceLocation channel, int version) { }

    public static <T extends Packet<T>> void registerS2CPacket(ResourceLocation channel, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        if (Environment.isClientSide()) {
            ClientPacketRegistry.registerS2CPacket(channelPath(channel, id), handler);
        }
    }

    public static <T extends Packet<T>> void registerC2SPacket(ResourceLocation channel, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        ServerPlayNetworking.registerGlobalReceiver(channelPath(channel, id), (server, player, handler1, buf, responseSender) -> {
            T decode = handler.decode(buf);
            server.execute(() -> handler.handle(decode).apply(player, player.getLevel()));
        });
    }

    public static <T extends Packet<T>> void sendToServer(ResourceLocation channel, T packet) {
        if (Environment.isClientSide()) {
            ClientPacketRegistry.sendToServer(channelPath(channel, packet.getId()), packet);
        }
    }

    public static <T extends Packet<T>> void sendToPlayer(ResourceLocation channel, T packet, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.getHandler().encode(packet, buf);
            ServerPlayNetworking.send(serverPlayer, channelPath(channel, packet.getId()), buf);
        }
    }

    private static ResourceLocation channelPath(ResourceLocation channel, ResourceLocation id) {
        return new ResourceLocation(channel.getNamespace(), channel.getPath() + "/" + id.getNamespace() + "/" + id.getPath());
    }
}