package com.blackgear.platform.core.util.network.client.forge;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public interface ChannelInfoHolder {
    Collection<ResourceLocation> getPendingChannelNames();
}