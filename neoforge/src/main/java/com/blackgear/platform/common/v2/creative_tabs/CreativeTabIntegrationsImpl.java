package com.blackgear.platform.common.v2.creative_tabs;

import com.blackgear.platform.Platform;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.List;

@EventBusSubscriber(modid = Platform.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class CreativeTabIntegrationsImpl {
    @SubscribeEvent
    public static void bootstrap(BuildCreativeModeTabContentsEvent event) {
        List<Modifier> modifiers = CreativeTabIntegrations.modifiers().get(event.getTabKey());
        
        if (modifiers == null || modifiers.isEmpty()) return;
        
        Output output = new Output() {
            @Override
            public boolean contains(ItemLike item) {
                return event.getParentEntries().stream().anyMatch(stack -> stack.is(item.asItem()));
            }
            
            @Override
            public void accept(ItemStack stack, CreativeModeTab.TabVisibility tabVisibility) {
                if (stack.isEmpty()) return;
                event.accept(stack.copyWithCount(1), tabVisibility);
            }
            
            @Override
            public void addAfter(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                if (stack.isEmpty()) return;
                if (target.isEmpty() || !this.contains(target)) {
                    event.accept(stack.copyWithCount(1), visibility);
                } else {
                    event.insertAfter(target, stack.copyWithCount(1), visibility);
                }
            }
            
            @Override
            public void addBefore(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                if (stack.isEmpty()) return;
                if (target.isEmpty() || !this.contains(target)) {
                    event.accept(stack.copyWithCount(1), visibility);
                } else {
                    event.insertBefore(target, stack.copyWithCount(1), visibility);
                }
            }
        };
        
        for (Modifier modifier : modifiers) {
            modifier.accept(event.getFlags(), output, event.hasPermissions());
        }
    }
}