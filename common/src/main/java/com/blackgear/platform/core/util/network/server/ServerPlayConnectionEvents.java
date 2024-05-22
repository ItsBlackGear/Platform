package com.blackgear.platform.core.util.network.server;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.network.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerPlayConnectionEvents {
    public static final Event<Init> INIT = Event.create(Init.class);
    public static final Event<Join> JOIN = Event.create(Join.class);
    public static final Event<Disconnect> DISCONNECT = Event.create(Disconnect.class);
    
    @FunctionalInterface
    public interface Init {
        void onPlayInit(ServerGamePacketListenerImpl handler, MinecraftServer server);
    }
    
    @FunctionalInterface
    public interface Join {
        void onPlayReady(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server);
    }
    
    @FunctionalInterface
    public interface Disconnect {
        void onPlayDisconnect(ServerGamePacketListenerImpl handler, MinecraftServer server);
    }
}