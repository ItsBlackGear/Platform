package com.blackgear.platform.common;

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
 * Utility class for creating and modifying creative tabs.
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