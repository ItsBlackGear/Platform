package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.block.BlockPropertiesExtension;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour {
    public BlockMixin(Properties properties) {
        super(properties);
    }
    
    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        if (this.properties instanceof BlockPropertiesExtension) {
            return ((BlockPropertiesExtension) this.properties).getPushReaction();
        }
        
        return super.getPistonPushReaction(state);
    }
    
    @Override
    public OffsetType getOffsetType() {
        if (this.properties instanceof BlockPropertiesExtension) {
            return ((BlockPropertiesExtension) this.properties).getOffsetType();
        }
        
        return super.getOffsetType();
    }
}