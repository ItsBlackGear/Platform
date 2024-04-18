package com.blackgear.platform.common.block.forge;

import com.blackgear.platform.common.block.ToolType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockPropertiesBuilderImpl {
    public static void registerToolType(BlockBehaviour.Properties properties, ToolType toolType, int level) {
        properties.harvestTool(net.minecraftforge.common.ToolType.get(toolType.name));
        properties.harvestLevel(level);
    }
}