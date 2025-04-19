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

    public static <T extends Packet<T>> void registerClientbound(ResourceLocation name, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        if (Environment.isClientSide()) {
            ClientPacketRegistry.registerClientbound(createChannel(name, id), handler);
        }
    }

    public static <T extends Packet<T>> void registerServerbound(ResourceLocation name, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        ServerPlayNetworking.registerGlobalReceiver(createChannel(name, id), (server, player, handler1, buf, sender) -> {
            T decode = handler.decode(buf);
            server.execute(() -> handler.handle(decode).apply(player, player.level()));
        });
    }

    public static <T extends Packet<T>> void sendToServer(ResourceLocation name, T packet) {
        if (Environment.isClientSide()) {
            ClientPacketRegistry.sendToServer(createChannel(name, packet.getId()), packet);
        }
    }

    public static <T extends Packet<T>> void sendToPlayer(ResourceLocation name, T packet, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.getHandler().encode(packet, buf);
            ServerPlayNetworking.send(serverPlayer, createChannel(name, packet.getId()), buf);
        }
    }

    private static ResourceLocation createChannel(ResourceLocation channel, ResourceLocation id) {
        return new ResourceLocation(channel.getNamespace(), channel.getPath() + "/" + id.getNamespace() + "/" + id.getPath());
    }
}