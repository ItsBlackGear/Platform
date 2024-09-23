package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.block.BlockPropertiesExtension;
import com.blackgear.platform.core.mixin.access.BlockBehaviourAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Unique private final BlockBehaviour.Properties properties = ((BlockBehaviourAccessor) this.getBlock()).getProperties();
    
    @Shadow public abstract Block getBlock();
    @Shadow protected abstract BlockState asState();
    
    @Inject(
        method = "getOffset",
        at = @At("HEAD"),
        cancellable = true
    )
    private void getOffset(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Vec3> cir) {
        Block block = this.getBlock();
        if (block.getOffsetType() != BlockBehaviour.OffsetType.NONE && this.properties instanceof BlockPropertiesExtension) {
            BlockPropertiesExtension properties = (BlockPropertiesExtension) this.properties;
            long seed = Mth.getSeed(pos.getX(), 0, pos.getZ());
            float maxHorizontalOffset = properties.getMaxHorizontalOffset().applyAsFloat(this.asState());
            double x = Mth.clamp(((double) ((float) (seed & 15L) / 15.0F) - 0.5) * 0.5, -maxHorizontalOffset, maxHorizontalOffset);
            double y = block.getOffsetType() == BlockBehaviour.OffsetType.XYZ
                ? ((double) ((float) (seed >> 4 & 15L) / 15.0F) - 1.0D) * (double)properties.getMaxVerticalOffset().applyAsFloat(this.asState())
                : 0.0;
            double z = Mth.clamp(((double) ((float) (seed >> 8 & 15L) / 15.0F) - 0.5) * 0.5, -maxHorizontalOffset, maxHorizontalOffset);
            
            cir.setReturnValue(new Vec3(x, y, z));
        }
    }
}