package com.blackgear.platform.core.mixin.access;

import net.minecraft.client.Camera;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Accessor
    BlockGetter getLevel();

    @Accessor
    void setXRot(float xRot);

    @Accessor
    void setYRot(float yRot);
}