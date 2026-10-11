package com.blackgear.platform.common.v2.creative_tabs;

import com.blackgear.platform.Platform;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Platform.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CreativeTabIntegrationsImpl {
    @SubscribeEvent
    public static void bootstrap(BuildCreativeModeTabContentsEvent event) {
        List<Modifier> modifiers = CreativeTabIntegrations.modifiers().get(event.getTabKey());
        
        if (modifiers == null || modifiers.isEmpty()) return;
        
        Output output = new Output() {
            @Override
            public boolean contains(ItemLike item) {
                return event.getEntries().contains(item.asItem().getDefaultInstance());
            }
            
            @Override
            public void accept(ItemStack stack, CreativeModeTab.TabVisibility tabVisibility) {
                if (stack.isEmpty()) return;
                event.getEntries().put(stack, tabVisibility);
            }
            
            @Override
            public void addAfter(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                if (stack.isEmpty()) return;
                if (target.isEmpty() || !this.contains(target)) {
                    event.getEntries().put(stack, visibility);
                } else {
                    event.getEntries().putAfter(target, stack, visibility);
                }
            }
            
            @Override
            public void addBefore(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                if (stack.isEmpty()) return;
                if (target.isEmpty() || !this.contains(target)) {
                    event.getEntries().put(stack, visibility);
                } else {
                    event.getEntries().putBefore(target, stack, visibility);
                }
            }
        };
        
        for (Modifier modifier : modifiers) {
            modifier.accept(event.getFlags(), output, event.hasPermissions());
        }
    }
}