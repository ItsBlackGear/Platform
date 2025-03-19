package com.blackgear.platform.core.network.fabric;

import com.blackgear.platform.core.network.base.Packet;
import com.blackgear.platform.core.network.base.PacketHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientPacketRegistry {
    protected static <T extends Packet<T>> void registerS2CPacket(ResourceLocation channel, PacketHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(channel, (client, handler1, buf, responseSender) -> {
            T decode = handler.decode(buf);
            client.execute(() -> handler.handle(decode).apply(client.player, client.level));
        });
    }

    protected static <T extends Packet<T>> void sendToServer(ResourceLocation channel, T packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.getHandler().encode(packet, buf);
        ClientPlayNetworking.send(channel, buf);
    }
}