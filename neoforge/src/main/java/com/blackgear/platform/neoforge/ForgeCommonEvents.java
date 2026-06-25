package com.blackgear.platform.neoforge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.events.EntityEvents;
import com.blackgear.platform.core.events.DataLifecycleEvents;
import com.blackgear.platform.core.events.DatapackSyncEvents;
import com.blackgear.platform.core.networking.ServerListenerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = EventBusSubscriber.Bus.GAME
)
public class ForgeCommonEvents {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onTagReload(TagsUpdatedEvent event) {
        DataLifecycleEvents.DATA_RELOAD.invoker().onReload(event.getRegistryAccess(), event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            ServerListenerEvents.JOIN.invoker().listener(((ServerPlayer) event.getEntity()).connection, event.getEntity().getServer());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (EntityEvents.ON_SPAWN.invoker().onSpawn(event.getEntity(), event.getLevel()).isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAttack(AttackEntityEvent event) {
        if (EntityEvents.ON_ATTACK.invoker().onAttack(event.getEntity(), event.getEntity().getLastDamageSource()).isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityDeath(LivingDeathEvent event) {
        if (EntityEvents.ON_REMOVE.invoker().onRemove(event.getEntity(), event.getSource()).isCancelled()) {
            event.setCanceled(true);
        }

        if (!EntityEvents.ON_DEATH.invoker().onDeath(event.getEntity(), event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            DatapackSyncEvents.EVENT.invoker().onSync(event.getPlayer());
        } else {
            event.getPlayerList().getPlayers().forEach(player -> DatapackSyncEvents.EVENT.invoker().onSync(player));
        }
    }
}