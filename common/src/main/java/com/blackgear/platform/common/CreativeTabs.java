package com.blackgear.platform.common;

import com.blackgear.platform.core.ModInstance;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class for creating and modifying {@link CreativeModeTab}.
 *
 * @author ItsBlackGear
 */
public final class CreativeTabs {
    public static final List<Map<BiConsumer<ItemStack, List<ItemStack>>, Offset>> MODIFICATIONS = Lists.newArrayList();

    /**
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
     * @param location the text component to be displayed as the title
     * @param icon the item stack to be displayed as the icon
     * @return custom creative tab
     */
    @ExpectPlatform
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon) {
        throw new AssertionError();
    }

    /**
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
     * @param location the text component to be displayed as the title
     * @param icon the item stack to be displayed as the icon
     * @param display the list of components to be displayed in order
     * @return custom creative tab
     */
    @ExpectPlatform
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon, Consumer<List<ItemStack>> display) {
        throw new AssertionError();
    }

    /**
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
     * It's recommended to implement this method on the post-common setup for {@link ModInstance}.
     * Otherwise, you may receive the "Registry Object not present" error on Forge.
     * </p>
     *
     * @param display the list of components to be displayed in order
     */
    public static void modify(BiConsumer<ItemStack, List<ItemStack>> display) {
        MODIFICATIONS.add(ImmutableMap.of(display, Offset.AFTER));
    }
    
    public static void modify(BiConsumer<ItemStack, List<ItemStack>> display, Offset offset) {
        MODIFICATIONS.add(ImmutableMap.of(display, offset));
    }
    
    public static void modify(Consumer<Output> output) {
        output.accept(new Output() {
            @Override
            public void addItem(ItemStack target, ItemStack stack, Offset offset) {
                modify((input, output) -> {
                    if (input.sameItem(target)) {
                        output.add(stack);
                    }
                }, offset);
            }
            
            @Override
            public void addItems(ItemStack target, Collection<ItemStack> stacks, Offset offset) {
                modify((input, output) -> {
                    if (input.sameItem(target)) {
                        output.addAll(stacks);
                    }
                }, offset);
            }
        });
    }
    
    public enum Offset {
        BEFORE(0),
        AFTER(1);
        
        public final int value;
        
        Offset(int offset) {
            this.value = offset;
        }
    }
    
    public interface Output {
        // After
        
        default void addAfter(ItemStack target, ItemStack stack) {
            this.addItem(target, stack, Offset.AFTER);
        }
        
        default void addAfter(ItemStack target, Collection<ItemStack> stacks) {
            this.addItems(target, stacks, Offset.AFTER);
        }
        
        default void addAfter(ItemStack target, ItemStack... stacks) {
            this.addItems(target, Lists.newArrayList(stacks), Offset.AFTER);
        }
        
        default void addAfter(ItemLike target, ItemLike stack) {
            this.addItem(new ItemStack(target), new ItemStack(stack), Offset.AFTER);
        }
        
        default void addAfter(ItemLike target, Collection<ItemLike> stacks) {
            this.addItems(new ItemStack(target), stacks.stream().map(ItemStack::new).collect(Collectors.toList()), Offset.AFTER);
        }
        
        default void addAfter(ItemLike target, ItemLike... stacks) {
            this.addItems(new ItemStack(target), Lists.newArrayList(stacks).stream().map(ItemStack::new).collect(Collectors.toList()), Offset.AFTER);
        }
        
        // Before
        
        default void addBefore(ItemStack target, ItemStack stack) {
            this.addItem(target, stack, Offset.BEFORE);
        }
        
        default void addBefore(ItemStack target, Collection<ItemStack> stacks) {
            this.addItems(target, stacks, Offset.BEFORE);
        }
        
        default void addBefore(ItemStack target, ItemStack... stacks) {
            this.addItems(target, Lists.newArrayList(stacks), Offset.BEFORE);
        }
        
        default void addBefore(ItemLike target, ItemLike stack) {
            this.addItem(new ItemStack(target), new ItemStack(stack), Offset.BEFORE);
        }
        
        default void addBefore(ItemLike target, Collection<ItemLike> stacks) {
            this.addItems(new ItemStack(target), stacks.stream().map(ItemStack::new).collect(Collectors.toList()), Offset.BEFORE);
        }
        
        default void addBefore(ItemLike target, ItemLike... stacks) {
            this.addItems(new ItemStack(target), Lists.newArrayList(stacks).stream().map(ItemStack::new).collect(Collectors.toList()), Offset.BEFORE);
        }
        
        // Implementation
        
        void addItem(ItemStack target, ItemStack stack, Offset offset);
        
        void addItems(ItemStack target, Collection<ItemStack> stacks, Offset offset);
    }
}