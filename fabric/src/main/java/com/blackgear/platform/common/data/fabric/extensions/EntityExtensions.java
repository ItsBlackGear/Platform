package com.blackgear.platform.common.data.fabric.extensions;

import net.minecraft.nbt.CompoundTag;

public interface EntityExtensions {
    default CompoundTag getCustomData() {
        throw new RuntimeException("this should be overriden via mixin");
    }
}