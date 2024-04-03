package com.blackgear.platform.common.block;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistry {
    @ExpectPlatform
    public static WoodType create(ResourceLocation location) {
        throw new AssertionError();
    }
}