package com.blackgear.platform.core.network.forge;

import com.blackgear.platform.core.network.base.NetworkDirection;
import com.blackgear.platform.core.network.base.Packet;
import com.blackgear.platform.core.network.base.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketRegistryImpl {
    public static final Map<ResourceLocation, ChannelHolder> CHANNELS = new ConcurrentHashMap<>();

    public static void registerChannel(ResourceLocation name, int version) {
        String protocol = Integer.toString(version);
        ChannelHolder channel = new ChannelHolder(NetworkRegistry.newSimpleChannel(name, () -> protocol, protocol::equals, protocol::equals));
        CHANNELS.put(name, channel);
    }

    public static <T extends Packet<T>> void registerS2CPacket(ResourceLocation channel, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        registerPacket(channel, handler, packet, NetworkDirection.S2C);
    }

    public static <T extends Packet<T>> void registerC2SPacket(ResourceLocation channel, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        registerPacket(channel, handler, packet, NetworkDirection.C2S);
    }

    private static <T extends Packet<T>> void registerPacket(ResourceLocation channel, PacketHandler<T> handler, Class<T> packet, NetworkDirection direction) {
        ChannelHolder holder = getChannelHolder(channel);
        holder.value().registerMessage(holder.incrementPackets(), packet, handler::encode, handler::decode, (msg, ctx) -> {
            ctx.get().enqueueWork(() -> {
                Player player = switch (direction) {
                    case S2C -> ctx.get().getSender() == null ? getLocalPlayer() : null;
                    case C2S -> ctx.get().getSender();
                };

                if (player != null) {
                    handler.handle(msg).apply(player, player.getLevel());
                }
            });

            ctx.get().setPacketHandled(true);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends Packet<T>> void sendToServer(ResourceLocation name, T packet) {
        getChannelHolder(name).value().sendToServer(packet);
    }

    public static <T extends Packet<T>> void sendToPlayer(ResourceLocation name, T packet, Player player) {
        ChannelHolder channel = getChannelHolder(name);
        if (player instanceof ServerPlayer serverPlayer) {
            channel.value().send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
        }
    }

    private static ChannelHolder getChannelHolder(ResourceLocation name) {
        return Optional.ofNullable(CHANNELS.get(name)).orElseThrow(() -> new IllegalStateException("Channel not registered: " + name));
    }

    @OnlyIn(Dist.CLIENT)
    private static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    public static class ChannelHolder {
        private final AtomicInteger packets = new AtomicInteger();
        private final SimpleChannel channel;

        public ChannelHolder(SimpleChannel channel) {
            this.channel = Objects.requireNonNull(channel, "Channel cannot be null");
        }

        public int incrementPackets() {
            return this.packets.getAndIncrement();
        }

        public SimpleChannel value() {
            return this.channel;
        }
    }
}