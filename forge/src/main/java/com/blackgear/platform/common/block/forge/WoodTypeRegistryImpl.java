package com.blackgear.platform.common.block.forge;

import com.blackgear.platform.core.Environment;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistryImpl {
    public static WoodType create(ResourceLocation location, BlockSetType blockSetType) {
        WoodType type = WoodType.register(new WoodType(location.toString(), blockSetType));
        
        if (Environment.isClientSide()) {
            Sheets.addWoodType(type);
        }
        
        return type;
    }
}