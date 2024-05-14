package com.blackgear.platform.core.util.network.client;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.mixin.core.networking.access.ConnectScreenAccessor;
import com.blackgear.platform.core.mixin.core.networking.access.MinecraftAccessor;
import com.blackgear.platform.core.util.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public class ClientNetworking {
    public static final GlobalReceiverRegistry<ClientLoginNetworking.LoginQueryRequestHandler> LOGIN = new GlobalReceiverRegistry<>();
    public static final GlobalReceiverRegistry<ClientPlayNetworking.PlayChannelHandler> PLAY = new GlobalReceiverRegistry<>();
    private static ClientPlayNetworkAddon currentPlayAddon;
    
    public static ClientPlayNetworkAddon getAddon(ClientPacketListener listener) {
        return (ClientPlayNetworkAddon) ((NetworkHandlerExtensions) listener).getAddon();
    }
    
    public static ClientLoginNetworkAddon getAddon(ClientHandshakePacketListenerImpl listener) {
        return (ClientLoginNetworkAddon) ((NetworkHandlerExtensions) listener).getAddon();
    }
    
    public static Packet<?> createPlayC2SPacket(ResourceLocation channel, FriendlyByteBuf buffer) {
        return new ServerboundCustomPayloadPacket(channel, buffer);
    }
    
    @Nullable
    public static Connection getLoginConnection() {
        final Connection connection = ((MinecraftAccessor) Minecraft.getInstance()).getPendingConnection();
        
        // Check if we are connecting to an integrated server. This will set the field on MinecraftClient
        if (connection != null) {
            return connection;
        } else {
            // We are probably connecting to a remote server.
            // Check if the ConnectScreen is the currentScreen to determine that:
            if (Minecraft.getInstance().screen instanceof ConnectScreen) {
                return ((ConnectScreenAccessor) Minecraft.getInstance().screen).getConnection();
            }
        }
        
        // We are not connected to a server at all.
        return null;
    }
    
    @Nullable
    public static ClientPlayNetworkAddon getClientPlayAddon() {
        // Since Minecraft can be a bit weird, we need to check for the play addon in a few ways:
        // If the client's player is set this will work
        if (Minecraft.getInstance().getConnection() != null) {
            currentPlayAddon = null; // Shouldn't need this anymore
            return ClientNetworking.getAddon(Minecraft.getInstance().getConnection());
        }
        
        // We haven't hit the end of onGameJoin yet, use our backing field here to access the network handler
        if (currentPlayAddon != null) {
            return currentPlayAddon;
        }
        
        // We are not in play stage
        return null;
    }
    
    public static void setClientPlayAddon(ClientPlayNetworkAddon addon) {
        currentPlayAddon = addon;
    }
    
    public static void bootstrap() {
        // Reference cleanup for the locally stored addon if we are disconnected
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            currentPlayAddon = null;
        });
        
        // Register a login query handler for early channel registration.
        ClientLoginNetworking.registerGlobalReceiver(Networking.EARLY_REGISTRATION_CHANNEL, (client, listener, buffer, exporter) -> {
            int n = buffer.readVarInt();
            List<ResourceLocation> ids = new ArrayList<>(n);
            
            for (int i = 0; i < n; i++) {
                ids.add(buffer.readResourceLocation());
            }
            
            ((ChannelInfoHolder) listener.getConnection()).getPendingChannelNames().addAll(ids);
            Platform.LOGGER.debug("Received accepted channels from the server");
            
            FriendlyByteBuf response = PacketByteBufs.create();
            Collection<ResourceLocation> channels = ClientPlayNetworking.getGlobalReceivers();
            response.writeVarInt(channels.size());
            
            for (ResourceLocation id : channels) {
                response.writeResourceLocation(id);
            }
            
            Platform.LOGGER.debug("Sent accepted channels to the server");
            return CompletableFuture.completedFuture(response);
        });
    }
}