package com.blackgear.platform.client.v2.render;

import com.blackgear.platform.core.util.event.ResultHolder;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BlockRendererRegistry {
  public static final Supplier<BlockRendererRegistry> INSTANCE = Suppliers.memoize(BlockRendererRegistry::new);
  private static final Map<Block, BlockRendererRegistry.Renderer> RENDERERS = new HashMap<>();

  public void register(Block block, BlockRendererRegistry.Renderer renderer) {
    RENDERERS.putIfAbsent(block, renderer);
  }

  @Nullable
  public BlockRendererRegistry.Renderer get(Block block) {
    return RENDERERS.get(block);
  }

  public Map<Block, BlockRendererRegistry.Renderer> getRenderers() {
    return RENDERERS;
  }

  public interface Renderer {
    ResultHolder<BakedModel> render(BlockState state, BlockModelShaper shaper);

    default Map<? extends Block, ResourceLocation> registerBlockStates() {
      return Map.of();
    }

    default boolean shouldUse() {
      return true;
    }
  }
}
