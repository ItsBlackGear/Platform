package com.blackgear.platform.core.network.packet;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.network.base.Packet;
import com.blackgear.platform.core.network.base.PacketContext;
import com.blackgear.platform.core.network.base.PacketHandler;
import com.blackgear.platform.core.util.config.fabric.ConfigTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundConfigSyncPacket(String name, byte[] data) implements Packet<ClientboundConfigSyncPacket> {
    public static final ResourceLocation ID = Platform.resource("clientbound_config_sync");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public PacketHandler<ClientboundConfigSyncPacket> getHandler() {
        return HANDLER;
    }

    public static final class Handler implements PacketHandler<ClientboundConfigSyncPacket> {
        @Override
        public void encode(ClientboundConfigSyncPacket packet, FriendlyByteBuf buf) {
            buf.writeUtf(packet.name);
            buf.writeByteArray(packet.data);
        }

        @Override
        public ClientboundConfigSyncPacket decode(FriendlyByteBuf buf) {
            return new ClientboundConfigSyncPacket(buf.readUtf(), buf.readByteArray());
        }

        @Override
        public PacketContext handle(ClientboundConfigSyncPacket packet) {
            return (player, level) -> ConfigTracker.INSTANCE.receiveSyncedConfig(packet);
        }
    }
}