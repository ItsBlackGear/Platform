package com.blackgear.platform.core.util.network.client;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.network.PacketSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

@Environment(EnvType.CLIENT)
public class ClientPlayConnectionEvents {
    public static final Event<Init> INIT = Event.create(Init.class);
    public static final Event<Join> JOIN = Event.create(Join.class);
    public static final Event<Disconnect> DISCONNECT = Event.create(Disconnect.class);
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface Init {
        void onPlayInit(ClientPacketListener handler, Minecraft client);
    }
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface Join {
        void onPlayReady(ClientPacketListener handler, PacketSender sender, Minecraft client);
    }
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface Disconnect {
        void onPlayDisconnect(ClientPacketListener handler, Minecraft client);
    }
}