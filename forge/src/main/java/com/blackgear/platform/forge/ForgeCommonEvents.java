package com.blackgear.platform.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.events.EntityEvents;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ForgeCommonEvents {
    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinWorldEvent event) {
        if (!EntityEvents.ON_SPAWN.invoker().spawn(event.getEntity(), event.getWorld())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityAttack(LivingAttackEvent event) {
        if (!EntityEvents.ON_ATTACK.invoker().attack(event.getEntity(), event.getSource(), event.getAmount())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (!EntityEvents.ON_DEATH.invoker().death(event.getEntity(), event.getSource())) {
            event.setCanceled(true);
        }
    }
}