package com.blackgear.platform.core.util.network.server;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.network.PacketSender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.List;

public class S2CPlayChannelEvents {
    public static final Event<Register> REGISTER = Event.create(Register.class);
    public static final Event<Unregister> UNREGISTER = Event.create(Unregister.class);
    
    @FunctionalInterface
    public interface Register {
        void onChannelRegister(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server, List<ResourceLocation> channels);
    }
    
    @FunctionalInterface
    public interface Unregister {
        void onChannelUnregister(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server, List<ResourceLocation> channels);
    }
}