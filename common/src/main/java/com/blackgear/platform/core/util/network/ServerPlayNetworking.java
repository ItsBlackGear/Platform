package com.blackgear.platform.core.util.network;

import com.blackgear.platform.core.network.PacketRegistry;
import com.blackgear.platform.core.network.listener.PacketSender;
import com.blackgear.platform.core.network.packet.NetworkPacketWrapper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerPlayNetworking {
    private static final Map<ResourceLocation, PlayChannelHandler> HANDLERS = new ConcurrentHashMap<>();

    public static void registerGlobalReceiver(ResourceLocation channel, PlayChannelHandler handler) {
        HANDLERS.put(channel, handler);
    }

    public static void send(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        PacketRegistry.sendToPlayer(id, new NetworkPacketWrapper(id, new FriendlyByteBuf(buf.copy())), player);
    }

    public static void handle(NetworkPacketWrapper message, ServerPlayer sender) {
        HANDLERS.get(message.packet()).receive(sender.server, sender, sender.connection, new FriendlyByteBuf(message.data()), null);
    }

    public interface PlayChannelHandler {
        void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender response);
    }
}