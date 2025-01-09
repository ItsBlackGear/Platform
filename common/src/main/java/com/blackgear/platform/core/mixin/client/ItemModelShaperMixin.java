package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.common.item.ItemTransformations;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModelShaper.class)
public abstract class ItemModelShaperMixin {
    @Shadow public abstract ModelManager getModelManager();
    
    @Inject(
        method = "getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void vb$getItemHeldModel(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        ModelManager modelManager = this.getModelManager();
        
        ItemTransformations.MODELS_IN_HAND.entrySet()
            .stream()
            .filter(models -> models.getKey().test(stack))
            .map(entry -> modelManager.getModel(entry.getValue()))
            .findFirst()
            .ifPresent(cir::setReturnValue);
    }
}