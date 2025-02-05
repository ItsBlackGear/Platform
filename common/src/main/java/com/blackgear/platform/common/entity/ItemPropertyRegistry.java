package com.blackgear.platform.common.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ItemPropertyRegistry {
    @ExpectPlatform
    public static ClampedItemPropertyFunction registerGeneric(ResourceLocation name, ClampedItemPropertyFunction property) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static ItemPropertyFunction registerGeneric(ResourceLocation name, ItemPropertyFunction property) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void registerCustomModelData(ItemPropertyFunction property) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void register(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
        throw new AssertionError();
    }
    
    @Nullable
    public static ItemPropertyFunction getProperty(Item item, ResourceLocation name) {
        return ItemProperties.getProperty(item, name);
    }
}