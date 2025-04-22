package com.blackgear.platform.core.mixin.access;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ItemProperties.class)
public interface ItemPropertiesAccessor {
    @Accessor
    static Map<ResourceLocation, ItemPropertyFunction> getGENERIC_PROPERTIES() {
        throw new UnsupportedOperationException();
    }
}
