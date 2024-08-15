package com.blackgear.platform.common.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public interface BlockPropertiesExtension {
    PushReaction getPushReaction();
    
    void setPushReaction(PushReaction reaction);
    
    BlockBehaviour.OffsetType getOffsetType();
    
    void setOffsetType(BlockBehaviour.OffsetType offset);
}