package com.blackgear.platform.core.util.network.server;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.network.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

public class ServerLoginConnectionEvents {
    public static final Event<Init> INIT = Event.create(Init.class);
    public static final Event<QueryStart> QUERY_START = Event.create(QueryStart.class);
    public static final Event<Disconnect> DISCONNECT = Event.create(Disconnect.class);
    
    @FunctionalInterface
    public interface Init {
        void onLoginInit(ServerLoginPacketListenerImpl handler, MinecraftServer server);
    }
    
    @FunctionalInterface
    public interface QueryStart {
        void onLoginStart(ServerLoginPacketListenerImpl handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer);
    }
    
    @FunctionalInterface
    public interface Disconnect {
        void onLoginDisconnect(ServerLoginPacketListenerImpl handler, MinecraftServer server);
    }
}