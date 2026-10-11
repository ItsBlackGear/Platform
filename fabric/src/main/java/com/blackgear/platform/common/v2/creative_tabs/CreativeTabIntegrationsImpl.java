package com.blackgear.platform.common.v2.creative_tabs;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CreativeTabIntegrationsImpl {
    private static final Set<ResourceKey<CreativeModeTab>> REGISTERED = new HashSet<>();
    
    public static void bootstrap() {
        CreativeTabIntegrations.modifiers().keySet().forEach(CreativeTabIntegrationsImpl::register);
    }
    
    public static void register(ResourceKey<CreativeModeTab> tab) {
        if (!REGISTERED.add(tab)) return;
        
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> {
            Output output = new Output() {
                @Override
                public boolean contains(ItemLike item) {
                    Item target = item.asItem();
                    
                    for (ItemStack stack : entries.getDisplayStacks()) {
                        if (stack.is(target)) {
                            return true;
                        }
                    }
                    
                    return false;
                }
                
                @Override
                public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                    if (stack.isEmpty()) return;
                    entries.accept(stack, visibility);
                }
                
                @Override
                public void addAfter(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                    if (stack.isEmpty()) return;
                    if (target.isEmpty()) {
                        entries.accept(stack, visibility);
                    } else {
                        entries.addAfter(target, List.of(stack), visibility);
                    }
                }
                
                @Override
                public void addBefore(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                    if (stack.isEmpty()) return;
                    if (target.isEmpty()) {
                        entries.accept(stack, visibility);
                    } else {
                        entries.addBefore(target, List.of(stack), visibility);
                    }
                }
            };
            
            List<Modifier> modifiers = CreativeTabIntegrations.modifiers().getOrDefault(tab, List.of());
            
            for (Modifier modifier : modifiers) {
                modifier.accept(entries.getEnabledFeatures(), output, entries.shouldShowOpRestrictedItems());
            }
        });
    }
}