package com.blackgear.platform.core.network.forge;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.network.base.Packet;
import com.blackgear.platform.core.network.base.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class PacketRegistryImpl {
    public static final Map<ResourceLocation, Channel> CHANNELS = new ConcurrentHashMap<>();

    public static void registerChannel(ResourceLocation name) {
        String protocol = Environment.getModVersion(name.getNamespace());
        Channel channel = new Channel(0, NetworkRegistry.newSimpleChannel(name, () -> protocol, protocol::equals, protocol::equals));
        CHANNELS.put(name, channel);
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends Packet<T>> void registerS2CPacket(ResourceLocation channel, ResourceLocation id, PacketHandler<T> handler, Class<T> clazz) {
        registerPacket(channel, handler, clazz, true);
    }

    public static <T extends Packet<T>> void registerC2SPacket(ResourceLocation channel, ResourceLocation id, PacketHandler<T> handler, Class<T> clazz) {
        registerPacket(channel, handler, clazz, false);
    }

    private static <T extends Packet<T>> void registerPacket(ResourceLocation id, PacketHandler<T> handler, Class<T> clazz, boolean isS2C) {
        Channel channel = channel(id);
        channel.value().registerMessage(channel.incrementPackets(), clazz, handler::encode, handler::decode, (msg, ctx) -> {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                Player player = isS2C
                    ? (context.getSender() == null ? Minecraft.getInstance().player : null)
                    : context.getSender();

                if (player != null) {
                    handler.handle(msg).apply(player, player.getLevel());
                }
            });

            context.setPacketHandled(true);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends Packet<T>> void sendToServer(ResourceLocation name, T packet) {
        channel(name).value().sendToServer(packet);
    }

    public static <T extends Packet<T>> void sendToPlayer(ResourceLocation name, T packet, Player player) {
        Channel channel = channel(name);
        if (player instanceof ServerPlayer serverPlayer) {
            channel.value().send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
        }
    }

    private static Channel channel(ResourceLocation name) {
        return Optional.ofNullable(CHANNELS.get(name)).orElseThrow(() -> new IllegalStateException("Channel not registered: " + name));
    }

    public static class Channel {
        private int packets;
        private final SimpleChannel channel;

        public Channel(int packets, SimpleChannel channel) {
            this.packets = packets;
            this.channel = channel;
        }

        public int incrementPackets() {
            return this.packets++;
        }

        public SimpleChannel value() {
            return this.channel;
        }
    }
}