package com.blackgear.platform.core.mixin.access;

import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Direction.class)
public interface DirectionAccessor {
    @Accessor
    static Map<String, Direction> getBY_NAME() {
        throw new UnsupportedOperationException();
    }
}
