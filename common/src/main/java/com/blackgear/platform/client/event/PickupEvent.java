package com.blackgear.platform.client.event;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class PickupEvent {
    public static final Event<PickEntity> PICK_ENTITY = Event.create(PickEntity.class);
    
    public interface PickEntity {
        void pickEntity(Context context);
    }
    
    public interface Context {
        Entity getEntity();
        
        void setStack(ItemStack stack);
    }
}