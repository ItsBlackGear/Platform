package com.blackgear.platform.common.item.fabric;

import com.blackgear.platform.core.mixin.access.ItemPropertiesAccessor;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemPropertyRegistryImpl {
    public static ClampedItemPropertyFunction registerGeneric(ResourceLocation name, ClampedItemPropertyFunction property) {
        return ItemProperties.registerGeneric(name, property);
    }
    
    public static ItemPropertyFunction registerGeneric(ResourceLocation name, ItemPropertyFunction property) {
        ItemPropertiesAccessor.getGENERIC_PROPERTIES().put(name, property);
        return property;
    }
    
    public static void registerCustomModelData(ItemPropertyFunction property) {
        ItemPropertiesAccessor.callRegisterCustomModelData(property);
    }
    
    public static void register(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
        ItemProperties.register(item, name, property);
    }
}
