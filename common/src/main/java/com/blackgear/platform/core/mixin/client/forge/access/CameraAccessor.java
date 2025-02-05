package com.blackgear.platform.core.mixin.client.forge.access;

import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Accessor
    BlockGetter getLevel();
    
    @Accessor
    BlockPos.MutableBlockPos getBlockPosition();
    
    @Accessor
    Vector3f getForwards();
    
    @Accessor
    Vector3f getUp();
    
    @Accessor
    Vector3f getLeft();
}
