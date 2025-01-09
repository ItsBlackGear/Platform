package com.blackgear.platform.client.event;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.world.entity.player.Player;

public class FovRenderEvent {
    public static final Event<FovRender> RENDER = Event.create(FovRender.class, callback -> (player, fov) -> {
        for(FovRender event : callback) {
            float newFov = event.setFov(player, fov);
            
            if (newFov != fov) {
                return newFov;
            }
        }
        
        return fov;
    });
    
    public interface FovRender {
        float setFov(Player player, float fov);
    }
}