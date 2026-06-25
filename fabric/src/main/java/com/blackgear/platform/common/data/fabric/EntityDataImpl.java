package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.common.data.fabric.extensions.EntityExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class EntityDataImpl {
    public static CompoundTag getPersistentData(Entity entity) {
        return ((EntityExtensions) entity).getCustomData();
    }
}