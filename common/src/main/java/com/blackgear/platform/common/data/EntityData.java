package com.blackgear.platform.common.data;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class EntityData {
    @ExpectPlatform
    public static CompoundTag getPersistentData(Entity entity) {
        throw new AssertionError();
    }
}