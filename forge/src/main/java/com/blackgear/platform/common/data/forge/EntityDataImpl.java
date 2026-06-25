package com.blackgear.platform.common.data.forge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class EntityDataImpl {
    public static CompoundTag getPersistentData(Entity entity) {
        return entity.getPersistentData();
    }
}