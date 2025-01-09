package com.blackgear.platform.common.events;

import com.blackgear.platform.core.events.EventCallback;
import com.blackgear.platform.core.util.event.Event;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface EntityEvents {
    Event<LivingSpawn> ON_SPAWN = Event.create(LivingSpawn.class);
    Event<LivingAttack> ON_ATTACK = Event.create(LivingAttack.class);
    Event<LivingDeath> ON_DEATH = Event.create(LivingDeath.class);

    interface LivingSpawn {
        void spawn(Entity entity, Level level, EventCallback callback);
    }

    interface LivingAttack {
        void attack(Entity entity, DamageSource source, float amount, EventCallback callback);
    }

    interface LivingDeath {
        void death(Entity entity, DamageSource source, EventCallback callback);
    }
}