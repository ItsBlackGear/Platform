package com.blackgear.platform.client.event;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.world.entity.player.Player;

public interface FovRendering {
    Event<FovRendering> EVENT = Event.create(FovRendering.class, callback -> (player, fov) -> {
        for (FovRendering render : callback) {
            float newFov = render.setFov(player, fov);
            if (newFov != fov) return newFov;
        }

        return fov;
    });

    float setFov(Player player, float fov);
}