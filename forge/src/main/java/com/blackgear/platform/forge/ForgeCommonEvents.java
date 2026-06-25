package com.blackgear.platform.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.events.CommandRegistrar;
import com.blackgear.platform.common.events.EntityEvents;
import com.blackgear.platform.common.events.TickEvents;
import com.blackgear.platform.core.events.DataLifecycleEvents;
import com.blackgear.platform.core.events.DatapackSyncEvents;
import com.blackgear.platform.core.network.listener.ServerListenerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ForgeCommonEvents {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        EntityEvents.ON_JOIN.invoker().handle(event.getEntity(), event.getLevel());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        EntityEvents.ON_LEAVE.invoker().handle(event.getEntity(), event.getLevel());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onCommandRegistry(RegisterCommandsEvent event) {
        CommandRegistrar.EVENT.invoker().register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            TickEvents.SERVER_TICK_PRE.invoker().handle(event.getServer());
        } else {
            TickEvents.SERVER_TICK_POST.invoker().handle(event.getServer());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            TickEvents.LEVEL_TICK_PRE.invoker().handle(event.level);
        } else {
            TickEvents.LEVEL_TICK_POST.invoker().handle(event.level);
        }
    }

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
    public static void onEntityAttack(LivingAttackEvent event) {
        if (EntityEvents.ON_ATTACK.invoker().onAttack(event.getEntity(), event.getSource(), event.getAmount()).isCancelled()) {
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