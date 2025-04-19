package com.blackgear.platform.common.forge;

import com.blackgear.platform.common.CreativeTabs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CreativeTabsImpl {
    private static final Set<Consumer<BuildCreativeModeTabContentsEvent>> MODIFICATIONS = ConcurrentHashMap.newKeySet();
    
    public static CreativeModeTab create(Consumer<CreativeModeTab.Builder> consumer) {
        CreativeModeTab.Builder builder = CreativeModeTab.builder();
        consumer.accept(builder);
        return builder.build();
    }
    
    public static void modify(ResourceKey<CreativeModeTab> key, CreativeTabs.Modifier modifier) {
        modify(BuiltInRegistries.CREATIVE_MODE_TAB.get(key), modifier);
    }
    
    public static void modify(CreativeModeTab tab, CreativeTabs.Modifier modifier) {
        MODIFICATIONS.add(event -> {
            if (event.getTab() == tab) {
                modifier.accept(event.getFlags(), new CreativeTabs.Output() {
                    @Override
                    public void addAfter(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                        if (target.isEmpty()) {
                            event.getEntries().put(stack, visibility);
                        } else {
                            event.getEntries().putAfter(target, stack, visibility);
                        }
                    }
                    
                    @Override
                    public void addBefore(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                        if (target.isEmpty()) {
                            event.getEntries().put(stack, visibility);
                        } else {
                            event.getEntries().putBefore(target, stack, visibility);
                        }
                    }
                }, event.hasPermissions());
            }
        });
    }
}