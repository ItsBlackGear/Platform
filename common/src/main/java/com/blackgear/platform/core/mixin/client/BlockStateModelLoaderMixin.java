package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.v2.render.BlockRendererRegistry;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {
  @Shadow
  protected abstract void loadBlockStateDefinitions(ResourceLocation blockStateId, StateDefinition<Block, BlockState> stateDefenition);

  @Inject(method = "loadAllBlockStates", at = @At("TAIL"))
  private void loadRendererBlockStates(CallbackInfo ci) {
    for (var renderer : BlockRendererRegistry.INSTANCE.get().getRenderers().entrySet()) {
      for (var entry : renderer.getValue().registerBlockStates().entrySet()) {
        this.loadBlockStateDefinitions(entry.getValue(), entry.getKey().getStateDefinition());
      }
    }
  }
}
