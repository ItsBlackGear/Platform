package com.blackgear.platform.core.network.packet;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.network.base.Packet;
import com.blackgear.platform.core.network.base.PacketContext;
import com.blackgear.platform.core.network.base.PacketHandler;
import com.blackgear.platform.core.util.config.ConfigLoader;
import com.blackgear.platform.core.util.config.ConfigTracker;
import com.blackgear.platform.core.util.config.ModConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ConfigSyncPacket(String name, byte[] data) implements Packet<ConfigSyncPacket> {
    public static final ResourceLocation ID = Platform.resource("config_sync");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public PacketHandler<ConfigSyncPacket> getHandler() {
        return HANDLER;
    }

    public static final class Handler implements PacketHandler<ConfigSyncPacket> {
        @Override
        public void encode(ConfigSyncPacket message, FriendlyByteBuf buf) {
            buf.writeUtf(message.name);
            buf.writeByteArray(message.data);
        }

        @Override
        public ConfigSyncPacket decode(FriendlyByteBuf buf) {
            return new ConfigSyncPacket(buf.readUtf(), buf.readByteArray());
        }

        @Override
        public PacketContext handle(ConfigSyncPacket message) {
            return (player, level) -> {
                ModConfig config = ConfigTracker.INSTANCE.fileMap().get(message.name);
                if (config != null) {
                    config.acceptSyncedConfig(message.data);
                } else {
                    ConfigLoader.LOGGER.error("Received config data for unknown config: {}", message.name);
                }
            };
        }
    }
}