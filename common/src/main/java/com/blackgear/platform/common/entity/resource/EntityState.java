package com.blackgear.platform.common.entity.resource;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.registry.PlatformRegistries;

import java.util.function.Supplier;

public record EntityState(String key) {
    public static final CoreRegistry<EntityState> REGISTRY = CoreRegistry.create(PlatformRegistries.ENTITY_STATES.registry(), Platform.MOD_ID);

    public static final Supplier<EntityState> IDLE = register("idle");
    public static final Supplier<EntityState> EATING = register("eating");
    public static final Supplier<EntityState> ATTACKING = register("attacking");
    public static final Supplier<EntityState> INTERACTING = register("interacting");
    public static final Supplier<EntityState> SLEEPING = register("sleeping");
    public static final Supplier<EntityState> DIGGING = register("digging");
    public static final Supplier<EntityState> FISHING = register("fishing");

    private static Supplier<EntityState> register(String key) {
        return REGISTRY.register(key, () -> new EntityState(key));
    }
}