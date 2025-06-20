package com.blackgear.platform.common.neoforge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.CreativeTabs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@EventBusSubscriber(modid = Platform.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CreativeTabsImpl {
    private static final Set<Consumer<BuildCreativeModeTabContentsEvent>> MODIFICATIONS = ConcurrentHashMap.newKeySet();
    
    public static CreativeModeTab create(Consumer<CreativeModeTab.Builder> consumer) {
        CreativeModeTab.Builder builder = CreativeModeTab.builder();
        consumer.accept(builder);
        return builder.build();
    }
    
    public static void modify(ResourceKey<CreativeModeTab> key, CreativeTabs.Modifier modifier) {
        MODIFICATIONS.add(event -> {
            if (event.getTabKey().equals(key)) {
                modifier.accept(event.getFlags(), new CreativeTabs.Output() {
                    @Override
                    public void addAfter(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                        event.insertAfter(target, stack, visibility);
                    }

                    @Override
                    public void addBefore(ItemStack target, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                        event.insertBefore(target, stack, visibility);
                    }
                }, event.hasPermissions());
            }
        });
    }

    @SubscribeEvent
    public static void onCreativeTabModify(BuildCreativeModeTabContentsEvent event) {
        MODIFICATIONS.forEach(consumer -> consumer.accept(event));
    }
}