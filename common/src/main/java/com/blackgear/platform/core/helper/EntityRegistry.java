package com.blackgear.platform.core.helper;

import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.mixin.access.ActivityAccessor;
import com.blackgear.platform.core.mixin.access.SensorTypeAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Optional;
import java.util.function.Supplier;

public class EntityRegistry {
    private final CoreRegistry<EntityType<?>> entities;
    private final CoreRegistry<Activity> activities;
    private final CoreRegistry<SensorType<?>> sensors;
    private final CoreRegistry<MemoryModuleType<?>> memories;

    public static EntityRegistry create(String modId) {
        return new EntityRegistry(modId);
    }

    private EntityRegistry(String modId) {
        this.entities = CoreRegistry.create(BuiltInRegistries.ENTITY_TYPE, modId);
        this.activities = CoreRegistry.create(BuiltInRegistries.ACTIVITY, modId);
        this.sensors = CoreRegistry.create(BuiltInRegistries.SENSOR_TYPE, modId);
        this.memories = CoreRegistry.create(BuiltInRegistries.MEMORY_MODULE_TYPE, modId);
    }

    public <T extends Entity> Supplier<EntityType<T>> entity(String name, EntityType.Builder<T> entity) {
        return this.entities.register(name, () -> entity.build(name));
    }

    public Supplier<Activity> activity(String name) {
        return this.activities.register(name, () -> ActivityAccessor.createActivity(name));
    }

    public <T extends Sensor<?>> Supplier<SensorType<T>> sensor(String name, Supplier<T> supplier) {
        return this.sensors.register(name, () -> SensorTypeAccessor.createSensorType(supplier));
    }

    public <T> Supplier<MemoryModuleType<T>> memory(String name, Codec<T> codec) {
        return this.memories.register(name, () -> new MemoryModuleType<>(Optional.of(codec)));
    }

    public <T> Supplier<MemoryModuleType<T>> memory(String name) {
        return this.memories.register(name, () -> new MemoryModuleType<>(Optional.empty()));
    }

    public void register() {
        this.entities.register();
        this.activities.register();
        this.sensors.register();
        this.memories.register();
    }
}