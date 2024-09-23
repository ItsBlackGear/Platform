package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.block.BlockPropertiesExtension;
import com.blackgear.platform.core.util.function.ToFloatFunction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockBehaviour.Properties.class)
public class BlockPropertiesMixin implements BlockPropertiesExtension {
    @Unique private PushReaction pushReaction = PushReaction.NORMAL;
    @Unique private BlockBehaviour.OffsetType offsetType = BlockBehaviour.OffsetType.NONE;
    @Unique private ToFloatFunction<BlockState> maxHorizontalOffset = state -> 0.25F;
    @Unique private ToFloatFunction<BlockState> maxVerticalOffset = state -> 0.2F;
    
    @Override
    public PushReaction getPushReaction() {
        return this.pushReaction;
    }
    
    @Override
    public void setPushReaction(PushReaction reaction) {
        this.pushReaction = reaction;
    }
    
    @Override
    public BlockBehaviour.OffsetType getOffsetType() {
        return this.offsetType;
    }
    
    @Override
    public void setOffsetType(BlockBehaviour.OffsetType offset) {
        this.offsetType = offset;
    }
    
    @Override
    public ToFloatFunction<BlockState> getMaxHorizontalOffset() {
        return this.maxHorizontalOffset;
    }
    
    @Override
    public void setMaxHorizontalOffset(ToFloatFunction<BlockState> offsetByState) {
        this.maxHorizontalOffset = offsetByState;
    }
    
    @Override
    public ToFloatFunction<BlockState> getMaxVerticalOffset() {
        return this.maxVerticalOffset;
    }
    
    @Override
    public void setMaxVerticalOffset(ToFloatFunction<BlockState> offsetByState) {
        this.maxVerticalOffset = offsetByState;
    }
}