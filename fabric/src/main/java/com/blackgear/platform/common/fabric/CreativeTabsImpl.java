package com.blackgear.platform.common.fabric;

import com.blackgear.platform.common.CreativeTabs;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class CreativeTabsImpl {
    public static CreativeModeTab create(Consumer<CreativeModeTab.Builder> consumer) {
        CreativeModeTab.Builder builder = FabricItemGroup.builder();
        consumer.accept(builder);
        return builder.build();
    }
    
    public static void modify(CreativeModeTab tab, CreativeTabs.Modifier modifier) {
        BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).ifPresent(key -> modify(key, modifier));
    }
    
    public static void modify(ResourceKey<CreativeModeTab> key, CreativeTabs.Modifier modifier) {
        ItemGroupEvents.modifyEntriesEvent(key).register(entries -> {
            modifier.accept(entries.getEnabledFeatures(), new CreativeTabs.Output() {
                @Override
                public void addAfter(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                    if (target.isEmpty()) {
                        entries.accept(stack, visibility);
                    } else {
                        entries.addAfter(target, List.of(stack), visibility);
                    }
                }
                
                @Override
                public void addBefore(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                    if (target.isEmpty()) {
                        entries.accept(stack, visibility);
                    } else {
                        entries.addBefore(target, List.of(stack), visibility);
                    }
                }
            }, entries.shouldShowOpRestrictedItems());
        });
    }
}