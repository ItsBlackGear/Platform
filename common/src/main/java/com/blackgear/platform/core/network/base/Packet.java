package com.blackgear.platform.core.network.base;

import net.minecraft.resources.ResourceLocation;

public interface Packet<T extends Packet<T>> {
    ResourceLocation getId();

    PacketHandler<T> getHandler();
}