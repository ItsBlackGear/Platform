package com.blackgear.platform.core.mixin.client.access;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.NearPlane.class)
public interface NearPlaneAccessor {
    @Accessor
    Vec3 getForward();
}
