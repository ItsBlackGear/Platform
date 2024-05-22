package com.blackgear.platform.core.util.network.client;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.network.PacketSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@Environment(EnvType.CLIENT)
public class C2SPlayChannelEvents {
    public static final Event<Register> REGISTER = Event.create(Register.class);
    public static final Event<Unregister> UNREGISTER = Event.create(Unregister.class);
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface Register {
        void onChannelRegister(ClientPacketListener handler, PacketSender sender, Minecraft client, List<ResourceLocation> channels);
    }
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface Unregister {
        void onChannelUnregister(ClientPacketListener handler, PacketSender sender, Minecraft client, List<ResourceLocation> channels);
    }
}