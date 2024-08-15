package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.block.BlockPropertiesExtension;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockBehaviour.Properties.class)
public class BlockPropertiesMixin implements BlockPropertiesExtension {
    @Unique private PushReaction pushReaction;
    @Unique private BlockBehaviour.OffsetType offsetType;
    
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
}