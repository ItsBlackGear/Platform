package com.blackgear.platform.common.block.entries;

import com.blackgear.platform.common.blockentity.PlatformSkullBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PlatformWallSkullBlock extends WallSkullBlock {
    public PlatformWallSkullBlock(SkullBlock.Type type, Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter level) {
        return new PlatformSkullBlockEntity();
    }
}