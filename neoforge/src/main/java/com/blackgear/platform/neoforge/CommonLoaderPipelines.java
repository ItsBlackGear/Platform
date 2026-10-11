package com.blackgear.platform.neoforge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.events.CommandRegistrar;
import com.blackgear.platform.common.events.EntityEvents;
import com.blackgear.platform.common.events.EntityTrackingEvents;
import com.blackgear.platform.common.events.TickEvents;
import com.blackgear.platform.core.events.DataLifecycleEvents;
import com.blackgear.platform.core.events.DatapackSyncEvents;
import com.blackgear.platform.core.networking.ServerListenerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.Objects;

@EventBusSubscriber(modid = Platform.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonLoaderPipelines {
    // BLOCK PIPELINES
    public static final EventPipeline<FurnaceFuelBurnTimeEvent> FURNACE_FUEL = new EventPipeline<>();
    public static final EventPipeline<PlayerInteractEvent.RightClickBlock> BLOCK_INTERACTION = new EventPipeline<>();
    
    @SubscribeEvent
    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        FURNACE_FUEL.dispatchCancelable(event);
    }
    
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BLOCK_INTERACTION.dispatchCancelable(event);
    }
    
    // MOB PIPELINES
    public static final EventPipeline<PlayerInteractEvent.EntityInteract> MOB_INTERACTION = new EventPipeline<>();
    
    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        MOB_INTERACTION.dispatchCancelable(event);
    }
    
    // TRADER PIPELINES
    public static final EventPipeline<VillagerTradesEvent> VILLAGER_TRADES = new EventPipeline<>();
    public static final EventPipeline<WandererTradesEvent> WANDERER_TRADES = new EventPipeline<>();
    
    @SubscribeEvent
    public static void onVillagerTrading(VillagerTradesEvent event) {
        VILLAGER_TRADES.dispatch(event);
    }
    
    @SubscribeEvent
    public static void onWandererTrading(WandererTradesEvent event) {
        WANDERER_TRADES.dispatch(event);
    }
    
    // RESOURCE LISTENING PIPELINES
    public static final EventPipeline<AddReloadListenerEvent> RESOURCE_LISTENERS = new EventPipeline<>();
    
    @SubscribeEvent
    public static void onResourceListen(AddReloadListenerEvent event) {
        RESOURCE_LISTENERS.dispatch(event);
    }
    
    // TICKING
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onServerPreTick(ServerTickEvent.Pre event) {
        TickEvents.SERVER_TICK_PRE.invoker().handle(event.getServer());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onServerPostTick(ServerTickEvent.Post event) {
        TickEvents.SERVER_TICK_POST.invoker().handle(event.getServer());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLevelPreTick(LevelTickEvent.Pre event) {
        TickEvents.LEVEL_TICK_PRE.invoker().handle(event.getLevel());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLevelPostTick(LevelTickEvent.Post event) {
        TickEvents.LEVEL_TICK_POST.invoker().handle(event.getLevel());
    }
    
    // ENTITY STATE
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        EntityEvents.ON_JOIN.invoker().handle(event.getEntity(), event.getLevel());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        EntityEvents.ON_LEAVE.invoker().handle(event.getEntity(), event.getLevel());
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
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            ServerListenerEvents.JOIN.invoker().listener(((ServerPlayer) event.getEntity()).connection, event.getEntity().getServer());
        }
    }
    
    // DATA
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onTagReload(TagsUpdatedEvent event) {
        DataLifecycleEvents.DATA_RELOAD.invoker().onReload(event.getRegistryAccess(), event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED);
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            DatapackSyncEvents.EVENT.invoker().onSync(event.getPlayer());
        } else {
            event.getPlayerList().getPlayers().stream()
                .filter(Objects::nonNull)
                .forEach(player -> DatapackSyncEvents.EVENT.invoker().onSync(player));
        }
    }
    
    // COMMAND
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandRegistrar.EVENT.invoker().register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }
    
    // PLAYER TRACKING
    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        EntityTrackingEvents.START_TRACKING.invoker().onTracking(event.getTarget(), event.getEntity());
    }
    
    @SubscribeEvent
    public static void onPlayerStopTracking(PlayerEvent.StartTracking event) {
        EntityTrackingEvents.STOP_TRACKING.invoker().onTracking(event.getTarget(), event.getEntity());
    }
}