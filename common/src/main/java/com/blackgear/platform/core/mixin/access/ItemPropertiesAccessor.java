package com.blackgear.platform.core.mixin.access;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemProperties.class)
public interface ItemPropertiesAccessor {
    @Invoker
    static ItemPropertyFunction callRegisterGeneric(ResourceLocation name, ItemPropertyFunction property) {
        throw new UnsupportedOperationException();
    }
    
    @Invoker
    static void callRegister(Item item, ResourceLocation name, ItemPropertyFunction property) {
        throw new UnsupportedOperationException();
    }
}
