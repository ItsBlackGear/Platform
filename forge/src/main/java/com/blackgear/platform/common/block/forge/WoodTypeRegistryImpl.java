package com.blackgear.platform.common.block.forge;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WoodTypeRegistryImpl {
    public static WoodType create(ResourceLocation location, BlockSetType blockSetType) {
        return WoodType.register(new WoodType(location.toString(), blockSetType));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerWoodType(WoodType type) {
        Sheets.addWoodType(type);
    }
}