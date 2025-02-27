package com.blackgear.platform.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.events.EntityEvents;
import com.blackgear.platform.core.events.DatapackSyncEvents;
import com.blackgear.platform.core.network.MessageHandler;
import com.blackgear.platform.core.network.listener.ServerListenerEvents;
import com.blackgear.platform.core.network.packet.NetworkPacketWrapper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ForgeCommonEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level.isClientSide) {
            ServerListenerEvents.JOIN.invoker().listener(
                ((ServerPlayer) event.getEntity()).connection,
                (id, data) -> MessageHandler.DEFAULT_CHANNEL.sendToPlayer(new NetworkPacketWrapper(id, data), event.getEntity()),
                event.getEntity().getServer()
            );
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (!EntityEvents.ON_SPAWN.invoker().onSpawn(event.getEntity(), event.getLevel())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityAttack(LivingAttackEvent event) {
        if (!EntityEvents.ON_ATTACK.invoker().onAttack(event.getEntity(), event.getSource(), event.getAmount())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (!EntityEvents.ON_DEATH.invoker().onDeath(event.getEntity(), event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            DatapackSyncEvents.EVENT.invoker().onSync(event.getPlayer());
        } else {
            event.getPlayerList().getPlayers().forEach(player -> DatapackSyncEvents.EVENT.invoker().onSync(player));
        }
    }
}