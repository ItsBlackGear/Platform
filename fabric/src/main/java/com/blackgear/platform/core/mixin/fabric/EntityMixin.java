package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.common.data.fabric.extensions.EntityExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtensions {
    @Unique private CompoundTag customData;

    @Inject(
        method = "saveWithoutId",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
        )
    )
    private void saveCustomData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if (this.customData != null && !this.customData.isEmpty()) {
            tag.put("ForgeData", this.customData);
        }
    }

    @Inject(
        method = "load",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
        )
    )
    private void loadCustomData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ForgeData")) {
            this.customData = tag.getCompound("ForgeData");
        }
    }

    @Override
    public CompoundTag getCustomData() {
        if (this.customData == null) {
            this.customData = new CompoundTag();
        }

        return this.customData;
    }
}