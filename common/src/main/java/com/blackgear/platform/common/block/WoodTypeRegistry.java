package com.blackgear.platform.common.block;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistry {
    @ExpectPlatform
    public static WoodType create(ResourceLocation location, BlockSetType blockSetType) {
        throw new AssertionError();
    }

    @ExpectPlatform @Environment(EnvType.CLIENT)
    public static void registerWoodType(WoodType type) {
        throw new AssertionError();
    }
}