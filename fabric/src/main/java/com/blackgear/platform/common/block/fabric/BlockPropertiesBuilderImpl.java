package com.blackgear.platform.common.block.fabric;

import com.blackgear.platform.common.block.ToolType;
import net.fabricmc.fabric.impl.object.builder.FabricBlockInternals;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockPropertiesBuilderImpl {
    public static void registerToolType(BlockBehaviour.Properties properties, ToolType toolType, int level) {
        FabricBlockInternals.computeExtraData(properties).addMiningLevel(toolType.tag.get(), level);
    }
}