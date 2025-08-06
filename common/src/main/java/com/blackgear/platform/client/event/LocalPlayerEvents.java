package com.blackgear.platform.client.event;

import com.blackgear.platform.core.util.event.Event;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface LocalPlayerEvents {
    Event<PlayerJoin> ON_LOGIN = Event.create(PlayerJoin.class);
    Event<PlayerLeave> ON_LOGOUT = Event.create(PlayerLeave.class);
    Event<PlayerRespawn> ON_RESPAWN = Event.create(PlayerRespawn.class);

    interface PlayerJoin {
        void onLogin(LocalPlayer player);
    }

    interface PlayerLeave {
        void onLogout(@Nullable LocalPlayer player);
    }

    interface PlayerRespawn {
        void onRespawn(LocalPlayer old, LocalPlayer player);
    }
}
