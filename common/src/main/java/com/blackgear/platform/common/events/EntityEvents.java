package com.blackgear.platform.common.events;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.function.Consumer;

public interface EntityEvents {
    Event<LivingSpawn> ON_SPAWN = Event.create(LivingSpawn.class, events -> (entity, level) -> Arrays.stream(events).allMatch(event -> event.onSpawn(entity, level)));
    Event<LivingAttack> ON_ATTACK = Event.create(LivingAttack.class, events -> (entity, source, amount) -> Arrays.stream(events).allMatch(event -> event.onAttack(entity, source, amount)));
    Event<LivingDeath> ON_DEATH = Event.create(LivingDeath.class, events -> (entity, source) -> Arrays.stream(events).allMatch(event -> event.onDeath(entity, source)));
    Event<EntityPickUp> ON_PICK = Event.create(EntityPickUp.class);

    interface LivingSpawn {
        boolean onSpawn(Entity entity, Level level);
    }

    interface LivingAttack {
        boolean onAttack(Entity entity, DamageSource source, float amount);
    }

    interface LivingDeath {
        boolean onDeath(Entity entity, DamageSource source);
    }

    interface EntityPickUp {
        void onPickUp(Entity entity, Consumer<ItemStack> stack);
    }
}