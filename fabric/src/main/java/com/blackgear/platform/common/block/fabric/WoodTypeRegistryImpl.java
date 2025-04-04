package com.blackgear.platform.common.block.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistryImpl {
    public static WoodType create(ResourceLocation location, BlockSetType blockSetType) {
        return WoodTypeRegistry.register(location, blockSetType);
    }

    @Environment(EnvType.CLIENT)
    public static void registerWoodType(WoodType type) {
    }
}