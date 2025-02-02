package com.blackgear.platform.common.events;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public interface EntityEvents {
    Event<LivingSpawn> ON_SPAWN = Event.create(LivingSpawn.class, events -> (entity, level) -> Arrays.stream(events).allMatch(event -> event.spawn(entity, level)));
    Event<LivingAttack> ON_ATTACK = Event.create(LivingAttack.class, events -> (entity, source, amount) -> Arrays.stream(events).allMatch(event -> event.attack(entity, source, amount)));
    Event<LivingDeath> ON_DEATH = Event.create(LivingDeath.class, events -> (entity, source) -> Arrays.stream(events).allMatch(event -> event.death(entity, source)));

    interface LivingSpawn {
        boolean spawn(Entity entity, Level level);
    }

    interface LivingAttack {
        boolean attack(Entity entity, DamageSource source, float amount);
    }

    interface LivingDeath {
        boolean death(Entity entity, DamageSource source);
    }
}