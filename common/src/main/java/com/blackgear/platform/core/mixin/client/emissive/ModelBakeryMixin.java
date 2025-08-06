package com.blackgear.platform.core.mixin.client.emissive;

import com.blackgear.platform.client.v2.emissive.EmissiveModelWrapper;
import com.blackgear.platform.client.v2.emissive.EmissiveModelWrapperHolder;
import net.minecraft.client.resources.model.ModelBakery;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin implements EmissiveModelWrapperHolder {
    @Unique private EmissiveModelWrapper wrapper;

    @Override
    public @Nullable EmissiveModelWrapper getModelWrapper() {
        return this.wrapper;
    }

    @Override
    public void setModelWrapper(@Nullable EmissiveModelWrapper wrapper) {
        this.wrapper = wrapper;
    }
}