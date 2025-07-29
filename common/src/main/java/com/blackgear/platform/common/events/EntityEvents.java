package com.blackgear.platform.common.events;

import com.blackgear.platform.core.util.event.CancellableResult;
import com.blackgear.platform.core.util.event.Event;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public interface EntityEvents {
    Event<LivingSpawn> ON_SPAWN = Event.cancellable(LivingSpawn.class);
    Event<LivingAttack> ON_ATTACK = Event.cancellable(LivingAttack.class);
    Event<LivingDeath> ON_DEATH = Event.cancellable(LivingDeath.class);
    Event<EntityPickUp> ON_PICK = Event.create(EntityPickUp.class);

    interface LivingSpawn {
        CancellableResult onSpawn(Entity entity, Level level);
    }

    interface LivingAttack {
        CancellableResult onAttack(Entity entity, DamageSource source, float amount);
    }

    interface LivingDeath {
        CancellableResult onDeath(Entity entity, DamageSource source);
    }

    interface EntityPickUp {
        void onPickUp(Entity entity, Consumer<ItemStack> stack);
    }
}