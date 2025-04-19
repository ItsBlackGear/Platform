package com.blackgear.platform.common.block.forge;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WoodTypeRegistryImpl {
    public static WoodType create(ResourceLocation location) {
        return WoodType.register(WoodType.create(location.toString()));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerWoodType(WoodType type) {
        Sheets.addWoodType(type);
    }
}