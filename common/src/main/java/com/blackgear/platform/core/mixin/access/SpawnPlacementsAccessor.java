package com.blackgear.platform.core.mixin.access;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SpawnPlacements.class)
public interface SpawnPlacementsAccessor {
    @Accessor
    static Map<EntityType<?>, SpawnPlacements.Data> getDATA_BY_TYPE() {
        throw new UnsupportedOperationException();
    }
}