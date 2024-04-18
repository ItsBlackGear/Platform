package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.common.block.fabric.WoodTypeRegistryImpl;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheets.class)
public class SheetsMixin {
    @Inject(
        method = "signTexture",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void platform$createSignMaterial(WoodType type, CallbackInfoReturnable<Material> cir) {
        if (type instanceof WoodTypeRegistryImpl.WoodTypeImpl) {
            WoodTypeRegistryImpl.WoodTypeImpl impl = (WoodTypeRegistryImpl.WoodTypeImpl) type;
            cir.setReturnValue(
                new Material(
                    Sheets.SIGN_SHEET,
                    new ResourceLocation(impl.getLocation().getNamespace(), "entity/signs/" + impl.getLocation().getPath())
                )
            );
        }
    }
}