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
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PacketRegistryImpl {
    public static final Map<ResourceLocation, ChannelHolder> CHANNELS = new ConcurrentHashMap<>();

    public static void registerChannel(ResourceLocation name, int version) {
        String protocol = Integer.toString(version);
        ChannelHolder holder = new ChannelHolder(0, NetworkRegistry.newSimpleChannel(name, () -> protocol, protocol::equals, protocol::equals));
        CHANNELS.put(name, holder);
    }

    @OnlyIn(Dist.CLIENT)
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
                Player player = direction == NetworkDirection.S2C
                    ? (ctx.get().getSender() == null ? getLocalPlayer() : null)
                    : ctx.get().getSender();

                if (player != null) {
                    handler.handle(msg).apply(player, player.level);
                }
            });

            ctx.get().setPacketHandled(true);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends Packet<T>> void sendToServer(ResourceLocation id, T packet) {
        getChannelHolder(id).value().sendToServer(packet);
    }

    public static <T extends Packet<T>> void sendToPlayer(ResourceLocation id, T packet, Player player) {
        ChannelHolder channel = getChannelHolder(id);
        if (player instanceof ServerPlayer) {
            channel.value().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), packet);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    private static ChannelHolder getChannelHolder(ResourceLocation name) {
        return Optional.ofNullable(CHANNELS.get(name)).orElseThrow(() -> new IllegalStateException("Channel not registered: " + name));
    }

    public static class ChannelHolder {
        private int packets;
        private final SimpleChannel channel;

        public ChannelHolder(int packets, SimpleChannel channel) {
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
