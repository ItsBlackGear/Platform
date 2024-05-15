package com.blackgear.platform.common;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Utility class for creating and modifying {@link CreativeModeTab}.
 *
 * @author ItsBlackGear
 */
public class CreativeTabs {
    /**
     * <p>Example of creating a creative tab:</p>
     *
     * <pre> {@code
     *
     * CoreRegistry<CreativeModeTab> TABS = CoreRegistry.create(BuiltinRegistries.CREATIVE_MODE_TAB, MOD_ID);
     *
     * ResourceKey<CreativeModeTab> CUSTOM_TAB = TABS.resource(
     *     "custom_tab",
     *     () -> CreativeTabs.create(builder -> {
     *         builder.title(Component.translatable("tab.custom_tab"));
     *         builder.icon(() -> new ItemStack(CUSTOM_ITEM));
     *         builder.displayItems((parameters, output) -> {
     *             output.accept(new ItemStack(CUSTOM_ITEM));
     *         });
     *     }));
     *
     * } </pre>
     *
     * @param consumer the building components of the creative tab
     * @return custom creative tab
     */
    @ExpectPlatform
    public static CreativeModeTab create(Consumer<CreativeModeTab.Builder> consumer) {
        throw new AssertionError();
    }
    
    /**
     * <p>Example of modifying a vanilla creative tab (recommended for adding items only):</p>
     *
     * <pre>{@code
     *
     * CreativeTabs.modify(
     *     CreativeModeTabs.getDefaultTab(),
     *     (flags, output, operatorBlocks) -> {
     *         output.addAllAfter(
     *             new ItemStack(Items.CHICKEN_SPAWN_EGG),
     *             List.of(
     *                 new ItemStack(Items.DUCK_SPAWN_EGG),
     *                 new ItemStack(Items.TURKEY_SPAWN_EGG)
     *             )
     *         );
     *     }
     * );
     *
     * }</pre>
     */
    @ExpectPlatform
    public static void modify(CreativeModeTab tab, Modifier modifier) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void modify(ResourceKey<CreativeModeTab> key, Modifier modifier) {
        throw new AssertionError();
    }
    
    public interface Modifier {
        void accept(FeatureFlagSet flags, Output output, boolean operatorBlocks);
    }
    
    public interface Output extends CreativeModeTab.Output {
        // ========== DEFAULT ===========
        
        @Override
        default void accept(ItemStack stack, CreativeModeTab.TabVisibility tabVisibility) {
            addAfter(ItemStack.EMPTY, stack, tabVisibility);
        }
        
        // ========== ADD AFTER ===========
        
        void addAfter(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility);
        
        default void addAfter(ItemStack target, ItemStack stack) {
            addAfter(target, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        default void addAllAfter(ItemStack target, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility) {
            stacks.forEach(stack -> addAfter(target, stack, visibility));
        }
        
        default void addAllAfter(ItemStack target, Collection<ItemStack> stacks) {
            stacks.forEach(stack -> addAfter(target, stack));
        }
        
        // ========== ADD BEFORE ===========
        
        void addBefore(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility);
        
        default void addBefore(ItemStack target, ItemStack stack) {
            addBefore(target, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        default void addAllBefore(ItemStack target, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility) {
            stacks.forEach(stack -> addBefore(target, stack, visibility));
        }
        
        default void addAllBefore(ItemStack target, Collection<ItemStack> stacks) {
            stacks.forEach(stack -> addBefore(target, stack));
        }
    }
}