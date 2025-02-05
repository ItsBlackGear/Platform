package com.blackgear.platform.core.mixin.access;

import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Supplier;

@Mixin(SensorType.class)
public interface SensorTypeAccessor {
    @Invoker("<init>")
    static <U extends Sensor<?>> SensorType<U> createSensorType(Supplier<U> sensor) {
        throw new UnsupportedOperationException();
    }
}