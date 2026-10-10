package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.v2.render.BlockRendererRegistry;
import com.blackgear.platform.core.util.event.ResultHolder;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {
  @Inject(method = "getBlockModel", at = @At("HEAD"), cancellable = true)
  private void getBlockModel(BlockState state, CallbackInfoReturnable<BakedModel> cir) {
    BlockRendererRegistry.Renderer renderer = BlockRendererRegistry.INSTANCE.get().get(state.getBlock());
    if (renderer != null && renderer.shouldUse()) {
      ResultHolder<BakedModel> result = renderer.render(state, (BlockModelShaper) (Object) this);
      if (result.isCancelled()) {
        cir.setReturnValue(result.getValue());
      }
    }
  }
}
