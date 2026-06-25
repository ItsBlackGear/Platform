package com.blackgear.platform.common.events;

import com.blackgear.platform.core.util.event.CancellableResult;
import com.blackgear.platform.core.util.event.Event;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.function.Consumer;

public interface EntityEvents {
    Event<Instance> ON_JOIN = Event.create(Instance.class);
    Event<Instance> ON_LEAVE = Event.create(Instance.class);

    Event<LivingSpawn> ON_SPAWN = Event.cancellable(LivingSpawn.class);
    Event<LivingAttack> ON_ATTACK = Event.cancellable(LivingAttack.class);
    Event<LivingRemove> ON_REMOVE = Event.cancellable(LivingRemove.class);
    Event<LivingDeath> ON_DEATH = Event.create(LivingDeath.class, events -> (entity, source) -> Arrays.stream(events).allMatch(event -> event.onDeath(entity, source)));
    Event<EntityPickUp> ON_PICK = Event.create(EntityPickUp.class);

    interface Instance {
        void handle(Entity entity, Level level);
    }

    interface LivingSpawn {
        CancellableResult onSpawn(Entity entity, Level level);
    }

    interface LivingAttack {
        CancellableResult onAttack(Entity entity, DamageSource source, float amount);
    }

    interface LivingDeath {
        boolean onDeath(Entity entity, DamageSource source);
    }

    interface LivingRemove {
        CancellableResult onRemove(Entity entity, DamageSource source);
    }

    interface EntityPickUp {
        void onPickUp(Entity entity, Consumer<ItemStack> stack);
    }
}