package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.data.DataTransformer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourceLocation.class)
public class ResourceLocationMixin {
    @Mutable @Shadow @Final private String namespace;
    @Mutable @Shadow @Final private String path;

    @Inject(
        method = "<init>(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation$Dummy;)V",
        at = @At("TAIL")
    )
    private void onInit(String namespace, String path, ResourceLocation.Dummy dummy, CallbackInfo ci) {
        if (!DataTransformer.shouldCheckNamespace()) return;

        ResourceLocation remapped = DataTransformer.applyTransformsIfPossible(namespace, path);
        if (remapped != null) {
            this.namespace = remapped.getNamespace();
            this.path = remapped.getPath();
        }
    }
}