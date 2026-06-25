package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.core.helper.fabric.DataSerializerRegistryImpl;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityDataSerializers.class)
public class EntityDataSerializersMixin {
    @Inject(method = "getSerializer", at = @At("TAIL"), cancellable = true)
    private static void getSerializer(int id, CallbackInfoReturnable<EntityDataSerializer<?>> cir) {
        EntityDataSerializer<?> serializer = cir.getReturnValue();
        if (serializer == null) cir.setReturnValue(DataSerializerRegistryImpl.getSerializer(id));
    }

    @Inject(method = "getSerializedId", at = @At("TAIL"), cancellable = true)
    private static void getSerializedId(EntityDataSerializer<?> serializer, CallbackInfoReturnable<Integer> cir) {
        int id = cir.getReturnValue();
        if (id < 0) cir.setReturnValue(DataSerializerRegistryImpl.getSerializedId(serializer, id));
    }
}