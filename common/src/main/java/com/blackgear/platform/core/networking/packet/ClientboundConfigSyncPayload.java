package com.blackgear.platform.core.networking.packet;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.networking.PayloadContext;
import com.blackgear.platform.core.util.config.ConfigTracker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundConfigSyncPayload(String name, byte[] data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundConfigSyncPayload> TYPE = new Type<>(Platform.resource("clientbound_config_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundConfigSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ClientboundConfigSyncPayload::name,
        ByteBufCodecs.BYTE_ARRAY, ClientboundConfigSyncPayload::data,
        ClientboundConfigSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handler(ClientboundConfigSyncPayload payload, PayloadContext context) {
        context.enqueueWork(() -> ConfigTracker.INSTANCE.receiveSyncedConfig(payload));
    }
}