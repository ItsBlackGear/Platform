package com.blackgear.platform.common;

import com.google.common.collect.Maps;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FireBlock;

/**
 * A class that provides methods for vanilla integration.
 */
public class IntegrationHandler {

    /**
     * Sets a block as flammable with specified flame odds and burn odds.
     *
     * @param block The block to be set as flammable.
     * @param flameOdds The odds that fire will be created around the block.
     * @param burnOdds The odds that the block will be destroyed by fire.
     *
     * @see FireBlock#bootStrap() for references.
     */
    public static void setFlammable(Block block, int flameOdds, int burnOdds) {
        ((FireBlock)Blocks.FIRE).setFlammable(block, flameOdds, burnOdds);
    }

    /**
     * Sets an item as compostable with a specified chance.
     *
     * @param entry The item to be set as compostable.
     * @param chance The chance that the item will turn into compost in a composter.
     *
     * @see ComposterBlock#bootStrap() for references.
     */
    public static void setCompostable(ItemLike entry, float chance) {
        ComposterBlock.COMPOSTABLES.put(entry.asItem(), chance);
    }

    /**
     * Sets a block as strippable, meaning it can be stripped by an axe to become another block.
     *
     * @param source The original block that can be stripped.
     * @param target The block that the source block becomes after being stripped.
     *
     * @see AxeItem#STRIPPABLES for references.
     */
    public static void setStrippable(Block source, Block target) {
        AxeItem.STRIPPABLES = Maps.newHashMap(AxeItem.STRIPPABLES);
        AxeItem.STRIPPABLES.put(source, target);
    }

    @ExpectPlatform
    public static void setAsFuel(ItemLike item, int burnTime) {
        throw new AssertionError();
    }

    /**
     * Adds a custom interaction to a block.
     *
     * @param interaction The custom interaction to be added.
     *
     * @see net.minecraft.world.item.Item#useOn(UseOnContext) () for references.
     */
    @ExpectPlatform
    public static void addInteraction(Interaction interaction) {
        throw new AssertionError();
    }

    public interface Interaction {
        InteractionResult of(UseOnContext context);
    }
}