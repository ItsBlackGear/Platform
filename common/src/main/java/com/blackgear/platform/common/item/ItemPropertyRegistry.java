package com.blackgear.platform.common.item;

import com.blackgear.platform.core.mixin.client.access.ItemPropertiesAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ItemPropertyRegistry {
    public static ItemPropertyFunction registerGeneric(ResourceLocation name, ItemPropertyFunction property) {
        return ItemPropertiesAccessor.callRegisterGeneric(name, property);
    }
    
    public static void register(Item item, ResourceLocation name, ItemPropertyFunction property) {
        ItemPropertiesAccessor.callRegister(item, name, property);
    }
    
    @Nullable
    public static ItemPropertyFunction getProperty(Item item, ResourceLocation name) {
        return ItemProperties.getProperty(item, name);
    }
}