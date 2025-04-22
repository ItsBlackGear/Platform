package com.blackgear.platform.core.networking;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.util.config.ConfigTracker;
import com.blackgear.platform.core.util.config.ModConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ConfigSyncPayload(String name, byte[] data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ConfigSyncPayload> TYPE = new Type<>(Platform.resource("config_sync_neo"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ConfigSyncPayload::name,
        ByteBufCodecs.BYTE_ARRAY, ConfigSyncPayload::data,
        ConfigSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handler(ConfigSyncPayload payload, PayloadContext context) {
        context.enqueueWork(() -> {
            ModConfig config = ConfigTracker.INSTANCE.fileMap().get(payload.name());
            if (config != null) {
                config.acceptSyncedConfig(payload.data());
            } else {
                Platform.LOGGER.error("Received config data for unknown config: {}", payload.name());
            }
        });
    }
}