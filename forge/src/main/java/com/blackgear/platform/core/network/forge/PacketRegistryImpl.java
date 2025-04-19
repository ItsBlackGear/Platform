package com.blackgear.platform.core.network.forge;

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

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class PacketRegistryImpl {
    public static final Map<ResourceLocation, Channel> CHANNELS = new ConcurrentHashMap<>();

    public static void registerChannel(ResourceLocation name, int version) {
        String protocol = Integer.toString(version);
        Channel channel = new Channel(0, NetworkRegistry.newSimpleChannel(name, () -> protocol, protocol::equals, protocol::equals));
        CHANNELS.put(name, channel);
    }

    public static <T extends Packet<T>> void registerClientbound(ResourceLocation name, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        Channel channel = CHANNELS.get(name);
        if (channel == null) {
            throw new IllegalStateException("Channel not registered: " + name);
        }

        channel.channel.registerMessage(++channel.packets, packet, handler::encode, handler::decode, (msg, ctx) -> {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                Player player = context.getSender() == null ? getLocalPlayer() : null;
                if (player != null) {
                    handler.handle(msg).apply(player, player.level());
                }
            });
            context.setPacketHandled(true);
        });
    }

    public static <T extends Packet<T>> void registerServerbound(ResourceLocation name, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        Channel channel = CHANNELS.get(name);
        if (channel == null) {
            throw new IllegalStateException("Channel not registered: " + name);
        }

        channel.channel.registerMessage(++channel.packets, packet, handler::encode, handler::decode, (msg, ctx) -> {
            NetworkEvent.Context context = ctx.get();
            Player player = context.getSender();
            if (player != null) {
                context.enqueueWork(() -> handler.handle(msg).apply(player, player.level()));
            }
            context.setPacketHandled(true);
        });
    }

    public static <T extends Packet<T>> void sendToServer(ResourceLocation name, T packet) {
        Channel channel = CHANNELS.get(name);
        if (channel == null) {
            throw new IllegalStateException("Channel not registered: " + name);
        }

        channel.channel.sendToServer(packet);
    }

    public static <T extends Packet<T>> void sendToPlayer(ResourceLocation name, T packet, Player player) {
        Channel channel = CHANNELS.get(name);
        if (channel == null) {
            throw new IllegalStateException("Channel not registered: " + name);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            channel.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    public static class Channel {
        private int packets;
        private final SimpleChannel channel;

        public Channel(int packets, SimpleChannel channel) {
            this.packets = packets;
            this.channel = channel;
        }
    }
}