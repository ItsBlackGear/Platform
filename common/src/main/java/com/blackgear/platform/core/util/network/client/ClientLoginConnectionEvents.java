package com.blackgear.platform.core.util.network.client;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.event.EventFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;

@Environment(EnvType.CLIENT)
public class ClientLoginConnectionEvents {
    public static final Event<Init> INIT = EventFactory.create(Init.class);
    public static final Event<QueryStart> QUERY_START = EventFactory.create(QueryStart.class);
    public static final Event<Disconnect> DISCONNECT = EventFactory.create(Disconnect.class);
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface Init {
        void onLoginStart(ClientHandshakePacketListenerImpl handler, Minecraft client);
    }
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface QueryStart {
        void onLoginQueryStart(ClientHandshakePacketListenerImpl handler, Minecraft client);
    }
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface Disconnect {
        void onLoginDisconnect(ClientHandshakePacketListenerImpl handler, Minecraft client);
    }
}