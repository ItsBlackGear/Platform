package com.blackgear.platform.common.block;

import com.blackgear.platform.core.util.function.ToFloatFunction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public interface BlockPropertiesExtension {
    PushReaction getPushReaction();
    
    void setPushReaction(PushReaction reaction);
    
    BlockBehaviour.OffsetType getOffsetType();
    
    void setOffsetType(BlockBehaviour.OffsetType offset);
    
    ToFloatFunction<BlockState> getMaxHorizontalOffset();
    
    void setMaxHorizontalOffset(ToFloatFunction<BlockState> offsetByState);
    
    ToFloatFunction<BlockState> getMaxVerticalOffset();
    
    void setMaxVerticalOffset(ToFloatFunction<BlockState> offsetByState);
}