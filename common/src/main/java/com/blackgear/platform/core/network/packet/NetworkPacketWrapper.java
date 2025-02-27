package com.blackgear.platform.core.network.packet;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.network.base.Packet;
import com.blackgear.platform.core.network.base.PacketContext;
import com.blackgear.platform.core.network.base.PacketHandler;
import com.blackgear.platform.core.util.network.ServerPlayNetworking;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record NetworkPacketWrapper(ResourceLocation packet, ByteBuf data) implements Packet<NetworkPacketWrapper> {
    public static final ResourceLocation ID = Platform.resource("packet_wrapper");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public PacketHandler<NetworkPacketWrapper> getHandler() {
        return HANDLER;
    }

    public static final class Handler implements PacketHandler<NetworkPacketWrapper> {
        @Override
        public void encode(NetworkPacketWrapper message, FriendlyByteBuf buf) {
            buf.writeResourceLocation(message.packet);
            buf.writeBytes(message.data);
        }

        @Override
        public NetworkPacketWrapper decode(FriendlyByteBuf buf) {
            return new NetworkPacketWrapper(buf.readResourceLocation(), buf.readBytes(buf.readableBytes()));
        }

        @Override
        public PacketContext handle(NetworkPacketWrapper message) {
            return (player, level) -> ServerPlayNetworking.handle(message, (ServerPlayer) player);
        }
    }
}