package com.blackgear.platform.common;

import com.google.common.collect.Maps;
import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A class that provides multiple methods for vanilla integration.
 *
 * @author ItsBlackGear
 */
public final class IntegrationHandler {
    public static final Object2FloatMap<BlockState> SHEARABLES = new Object2FloatOpenHashMap<>();
    public static final Object2FloatMap<BlockState> SWORDABLES = new Object2FloatOpenHashMap<>();

    public static void setShearable(Block block, float destroySpeed) {
        SHEARABLES.put(block.defaultBlockState(), destroySpeed);
    }

    public static void setShearable(BlockState state, float destroySpeed) {
        SHEARABLES.put(state, destroySpeed);
    }

    public static void setSwordable(Block block, float destroySpeed) {
        SWORDABLES.put(block.defaultBlockState(), destroySpeed);
    }

    public static void setSwordable(BlockState state, float destroySpeed) {
        SWORDABLES.put(state, destroySpeed);
    }

    public static void setFlattenable(Block from, BlockState to) {
        ShovelItem.FLATTENABLES.put(from, to);
    }

    /**
     * <p>Example of creating a flammable block:</p>
     *
     * <pre> {@code
     *
     * IntegrationHandler.setFlammable(Blocks.OAK_PLANKS, 5, 20);
     *
     * } </pre>
     *
     * In the example we can see how we register the oak planks as a flammable block
     * with a 5% chance of creating fire around it and a 20% chance of being destroyed by fire.
     *
     * @param block The block to be set as flammable.
     * @param encouragement The odds that fire will be created around the block.
     * @param flammability The odds that the block will be destroyed by fire.
     *
     * @see FireBlock#bootStrap() for references.
     */
    public static void setFlammable(Block block, int encouragement, int flammability) {
        ((FireBlock) Blocks.FIRE).setFlammable(block, encouragement, flammability);
    }

    /**
     * <p>Example of creating a compostable item:</p>
     *
     * <pre> {@code
     *
     * IntegrationHandler.setCompostable(Items.WHEAT, 0.65F);
     *
     * } </pre>
     *
     * In the example we can see how we register the wheat item as compostable with a 65% chance of turning into compost.
     *
     * @param entry The item to be set as compostable.
     * @param chance The chance that the item will turn into compost in a composter.
     */
    public static void setCompostable(ItemLike entry, float chance) {
        ComposterBlock.COMPOSTABLES.put(entry.asItem(), chance);
    }

    /**
     * <p>Example of creating a strippable block:</p>
     *
     * <pre> {@code
     *
     * IntegrationHandler.setStrippable(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG);
     *
     * } </pre>
     *
     * In the example we can see how we register the oak log as a block that can be stripped into a stripped oak log.
     *
     * @param source The original block that can be stripped.
     * @param target The block that the source block becomes after being stripped.
     */
    public static void setStrippable(Block source, Block target) {
        AxeItem.STRIPPABLES = Maps.newHashMap(AxeItem.STRIPPABLES);
        AxeItem.STRIPPABLES.put(source, target);
    }

    /**
     * <p>Example of creating a fuel item:</p>
     *
     * <pre> {@code
     *
     * IntegrationHandler.addFuel(Items.LAVA_BUCKET, 20000);
     *
     * } </pre>
     *
     * In the example we can see how we register the lava bucket as a fuel that will burn for 20000 ticks (1000 seconds).
     *
     * @param entry The item to be set as fuel.
     * @param burnTime The time in ticks that the item will burn in a furnace.
     */
    @ExpectPlatform
    public static void addFuel(ItemLike entry, int burnTime) {
        throw new AssertionError();
    }

    /**
     * <p>Example of creating a fuel item:</p>
     *
     * <pre> {@code
     *
     *  IntegrationHandler.addDispenserBehavior(Items.ARROW, new AbstractProjectileDispenseBehavior() {
     *      @Override
     *      protected Projectile getProjectile(
     *          Level level,
     *          Position position,
     *          ItemStack stack
     *      ) {
     *          Arrow arrow = new Arrow(level, position.x(), position.y(), position.z());
     *          arrow.pickup = AbstractArrow.Pickup.ALLOWED;
     *          return arrow;
     *      }
     *  });
     *
     * } </pre>
     *
     * In the example we can see how we register the behavior of a dispenser shooting arrows.
     *
     * @param entry The item or block to register the behavior to.
     * @param behavior The dispenser behavior to be added.
     */
    public static void addDispenserBehavior(ItemLike entry, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(entry.asItem(), behavior);
    }

    /**
     * <p>Example of creating a custom interaction:</p>
     *
     * <pre> {@code
     *
     *  IntegrationHandler.addInteraction(context -> {
     *      ItemStack stack = context.getItemInHand();
     *      Item item = stack.getItem();
     *      Level level = context.getLevel();
     *      BlockPos pos = context.getClickedPos();
     *      BlockState state = level.getBlockState(pos);
     *      Player player = context.getPlayer();
     *
     *      if (state.getBlock() instanceof CakeBlock) {
     *          if (state.getValue(CakeBlock.BITES) == 0) {
     *              Block block = Block.byItem(item);
     *              if (block instanceof CandleBlock && player != null) {
     *                  if (!player.isCreative()) {
     *                      stack.shrink(1);
     *                  }
     *
     *                  level.playSound(
     *                      null,
     *                      pos,
     *                      SoundEvents.CAKE_ADD_CANDLE,
     *                      SoundSource.BLOCKS,
     *                      1.0F,
     *                      1.0F
     *                  );
     *                  level.setBlockAndUpdate(pos, CandleCakeBlock.byCandle(block));
     *                  player.awardStat(Stats.ITEM_USED.get(item));
     *
     *                  return InteractionResult.SUCCESS;
     *              }
     *          }
     *      }
     *
     *      return InteractionResult.PASS;
     *  });
     *
     * } </pre>
     *
     * In the example we can see how we register the behavior of a player adding a candle to a cake.
     *
     * @param interaction The custom interaction to be added.
     */
    @ExpectPlatform
    public static void addInteraction(Interaction interaction) {
        throw new AssertionError();
    }

    public interface Interaction {
        InteractionResult of(UseOnContext context);
    }
}