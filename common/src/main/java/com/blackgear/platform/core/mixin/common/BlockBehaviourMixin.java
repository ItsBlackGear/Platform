package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.block.BlockPropertiesExtension;
import com.blackgear.platform.core.mixin.common.access.BlockBehaviourAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @Unique private BlockBehaviour.Properties properties = ((BlockBehaviourAccessor) this).getProperties();
    
    @Inject(
        method = "getPistonPushReaction",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$pushReaction(BlockState state, CallbackInfoReturnable<PushReaction> cir) {
        if (this.properties instanceof BlockPropertiesExtension) {
            cir.setReturnValue(((BlockPropertiesExtension) this.properties).getPushReaction());
        }
    }
    
    @Inject(
        method = "getOffsetType",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$offsetType(CallbackInfoReturnable<BlockBehaviour.OffsetType> cir) {
        if (this.properties instanceof BlockPropertiesExtension) {
            cir.setReturnValue(((BlockPropertiesExtension) this.properties).getOffsetType());
        }
    }
}