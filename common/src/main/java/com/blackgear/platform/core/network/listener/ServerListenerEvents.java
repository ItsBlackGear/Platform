package com.blackgear.platform.core.network.listener;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public interface ServerListenerEvents {
    Event<ServerListenerEvents> JOIN = Event.create(ServerListenerEvents.class);

    void listener(ServerGamePacketListenerImpl connection, PacketSender sender, MinecraftServer player);
}