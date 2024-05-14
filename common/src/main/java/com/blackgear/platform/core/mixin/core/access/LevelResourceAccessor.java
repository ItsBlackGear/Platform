package com.blackgear.platform.core.mixin.core.access;

import net.minecraft.world.level.storage.LevelResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelResource.class)
public interface LevelResourceAccessor {
    @Invoker("<init>")
    static LevelResource createLevelResource(String string) {
        throw new UnsupportedOperationException();
    }
}
