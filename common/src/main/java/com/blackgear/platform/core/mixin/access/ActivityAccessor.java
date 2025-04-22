package com.blackgear.platform.core.mixin.access;

import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Activity.class)
public interface ActivityAccessor {
    @Invoker("<init>")
    static Activity createActivity(String name) {
        throw new AssertionError();
    }
}