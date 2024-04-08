package com.blackgear.platform.common;

import com.blackgear.platform.core.ModInstance;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for creating and modifying {@link CreativeModeTab}.
 *
 * <p>Example of creating an empty creative tab (recommended for modded item-only tabs):</p>
 *
 * <pre> {@code
 *
 * CreativeModeTab CUSTOM_TAB = CreativeTabs.create(
 * 	new ResourceLocation("mod_id", "custom_tab"),
 * 	() -> new ItemStack(CUSTOM_ITEM);
 * );
 *
 * // Use item properties to assign the item to the tab. Otherwise, it won't appear on creative inventory
 * // search result.
 * new Item(new Item.Properties().tab(CUSTOM_TAB));
 *
 * } </pre>
 *
 * <p>Example of creating a creative tab with item stacks:</p>
 *
 * <pre>{@code
 *
 * CreativeModeTab CUSTOM_TAB = CreativeTabs.create(
 *     new ResourceLocation("mod_id", "custom_tab"),
 *     () -> new ItemStack(CUSTOM_ITEM),
 *     stacks -> {
 *         stacks.add(new ItemStack(Items.OAK_PLANKS));
 *         stacks.add(new ItemStack(Items.OAK_STAIRS));
 *         stacks.add(new ItemStack(Items.OAK_SLABS));
 *     }
 * );
 *
 * }</pre>
 *
 * <p>Example of modifying a vanilla creative tab (recommended for adding items only):</p>
 *
 * <pre>{@code
 *
 * CreativeTabs.modify((stack, stacks) -> {
 *     if (stack.is(Items.CHICKEN_SPAWN_EGG)) {
 *         stacks.add(new ItemStack(DUCK_SPAWN_EGG));
 *         stacks.add(new ItemStack(TURKEY_SPAWN_EGG));
 *     }
 * });
 *
 * }</pre>
 *
 * <p>
 * It's recommended to implement the "modify" method on the post-common setup for {@link ModInstance}.
 * Otherwise, you may receive the "Registry Object not present" error on Forge.
 * </p>
 *
 * @author ItsBlackGear
 */
public class CreativeTabs {
    public static final List<BiConsumer<ItemStack, List<ItemStack>>> MODIFICATIONS = Lists.newArrayList();

    @ExpectPlatform
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon, Consumer<List<ItemStack>> display) {
        throw new AssertionError();
    }

    public static void modify(BiConsumer<ItemStack, List<ItemStack>> display) {
        MODIFICATIONS.add(display);
    }
}