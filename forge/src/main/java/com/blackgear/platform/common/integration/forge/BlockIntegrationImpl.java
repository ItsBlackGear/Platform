package com.blackgear.platform.common.integration.forge;

import com.blackgear.platform.common.integration.BlockIntegration;
import com.blackgear.platform.common.integration.BlockInteraction;
import com.blackgear.platform.core.util.EventBus;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockIntegrationImpl {
    private static final Unsafe UNSAFE;

    public static void registerIntegrations(Consumer<BlockIntegration.Event> listener) {
        listener.accept(new BlockIntegration.Event() {
            @Override
            public void registerBlockInteraction(BlockInteraction interaction) {
                EventBus.get(EventBus.MOD).addListener((PlayerInteractEvent.RightClickBlock event) -> {
                    InteractionResult result = interaction.onUse(new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
                    if (result != InteractionResult.PASS) {
                        event.setCanceled(true);
                        event.setCancellationResult(result);
                    }
                });
            }

            @Override
            public void registerFuelItem(ItemLike item, int burnTime) {
                EventBus.get(EventBus.LOADER).addListener((FurnaceFuelBurnTimeEvent event) -> {
                    if (event.getItemStack().is(item.asItem())) {
                        event.setBurnTime(burnTime);
                    }
                });
            }

            @Override
            public void registerCompostableItem(ItemLike item, float chance) {
                ComposterBlock.COMPOSTABLES.putIfAbsent(item.asItem(), chance);
            }

            @Override
            public void registerOxidableBlock(Block less, Block more) {
                Map<Block, Block> mutable = new HashMap<>(WeatheringCopper.NEXT_BY_BLOCK.get());
                mutable.put(less, more);
                ImmutableBiMap<Block, Block> updated = ImmutableBiMap.copyOf(mutable);
                Supplier<?> current = WeatheringCopper.NEXT_BY_BLOCK;
                setInterfaceSupplierField(WeatheringCopper.class, current, () -> updated);
            }

            @Override
            public void registerWaxableBlock(Block unwaxed, Block waxed) {
                Map<Block, Block> mutable = new HashMap<>(HoneycombItem.WAXABLES.get());
                mutable.put(unwaxed, waxed);
                ImmutableBiMap<Block, Block> updated = ImmutableBiMap.copyOf(mutable);
                HoneycombItem.WAXABLES = () -> updated;
            }
        });
    }

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to obtain Unsafe", e);
        }
    }

    private static void setInterfaceSupplierField(Class<?> iface, Supplier<?> currentValue, Supplier<?> newValue) {
        for (Field f : iface.getDeclaredFields()) {
            try {
                if (f.getType() == Supplier.class && f.get(null) == currentValue) {
                    UNSAFE.putObject(UNSAFE.staticFieldBase(f), UNSAFE.staticFieldOffset(f), newValue);
                    return;
                }
            } catch (IllegalAccessException ignored) {}
        }
        throw new RuntimeException("Could not find Supplier field on " + iface.getSimpleName() + " matching the given value");
    }
}