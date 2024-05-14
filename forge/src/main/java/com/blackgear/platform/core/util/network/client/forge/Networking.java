package com.blackgear.platform.core.util.network.client.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.util.network.PacketByteBufs;
import com.blackgear.platform.core.util.network.server.ServerLoginConnectionEvents;
import com.blackgear.platform.core.util.network.server.ServerLoginNetworking;
import com.blackgear.platform.core.util.network.server.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Networking {
    public static final ResourceLocation REGISTER_CHANNEL = new ResourceLocation(Platform.MOD_ID, "register");
    public static final ResourceLocation UNREGISTER_CHANNEL = new ResourceLocation(Platform.MOD_ID, "unregister");
    public static final ResourceLocation EARLY_REGISTRATION_CHANNEL = new ResourceLocation(Platform.MOD_ID, "early_registration");
    
    public static void bootstrap() {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            // Send early registration packet
            FriendlyByteBuf buffer = PacketByteBufs.create();
            Collection<ResourceLocation> channels = ServerPlayNetworking.getGlobalReceivers();
            buffer.writeVarInt(channels.size());
            
            for (ResourceLocation channel : channels) {
                buffer.writeResourceLocation(channel);
            }
            
            sender.sendPacket(EARLY_REGISTRATION_CHANNEL, buffer);
            Platform.LOGGER.debug("Sent accepted channels to the client for \"{}\"", handler.getUserName());
        });
        
        ServerLoginNetworking.registerGlobalReceiver(EARLY_REGISTRATION_CHANNEL, (server, handler, understood, buf, synchronizer, sender) -> {
            if (!understood) {
                // The client is likely a vanilla client.
                return;
            }
            
            int n = buf.readVarInt();
            List<ResourceLocation> channels = new ArrayList<>(n);
            
            for (int i = 0; i < n; i++) {
                channels.add(buf.readResourceLocation());
            }
            
            ((ChannelInfoHolder) handler.getConnection()).getPendingChannelNames().addAll(channels);
            Platform.LOGGER.debug("Received accepted channels from the client for \"{}\"", handler.getUserName());
        });
    }
    
    public static boolean isReservedPlayChannel(ResourceLocation channel) {
        return channel.equals(REGISTER_CHANNEL) || channel.equals(UNREGISTER_CHANNEL);
    }
}