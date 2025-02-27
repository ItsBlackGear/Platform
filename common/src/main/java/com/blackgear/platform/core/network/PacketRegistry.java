package com.blackgear.platform.core.network;

import com.blackgear.platform.core.network.base.Packet;
import com.blackgear.platform.core.network.base.PacketHandler;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PacketRegistry {
    @ExpectPlatform
    public static void registerChannel(ResourceLocation channel, int version) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Packet<T>> void registerS2CPacket(ResourceLocation channel, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Packet<T>> void registerC2SPacket(ResourceLocation channel, ResourceLocation id, PacketHandler<T> handler, Class<T> packet) {
        throw new AssertionError();
    }

    @ExpectPlatform @Environment(EnvType.CLIENT)
    public static <T extends Packet<T>> void sendToServer(ResourceLocation id, T packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Packet<T>> void sendToPlayer(ResourceLocation id, T packet, Player player) {
        throw new AssertionError();
    }
}