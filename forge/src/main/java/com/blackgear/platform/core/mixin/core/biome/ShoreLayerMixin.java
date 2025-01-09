package com.blackgear.platform.core.mixin.core.biome;

import com.blackgear.platform.common.worldgen.biome.InternalBiomeData;
import com.blackgear.platform.common.worldgen.biome.InternalBiomeUtils;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.ShoreLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShoreLayer.class)
public class ShoreLayerMixin {
    @Inject(
        method = "apply",
        at = @At("HEAD"),
        cancellable = true
    )
    private void apply(Context context, int north, int east, int south, int west, int center, CallbackInfoReturnable<Integer> cir) {
        ResourceKey<Biome> centerBiome = Biomes.byId(center);
        
        if (InternalBiomeData.getOverworldShores().containsKey(centerBiome) && InternalBiomeUtils.neighborsOcean(north, east, south, west)) {
            cir.setReturnValue(InternalBiomeUtils.getRawId(InternalBiomeData.getOverworldShores().get(centerBiome).pickRandom(context)));
        } else if (InternalBiomeData.getOverworldEdges().containsKey(centerBiome) && InternalBiomeUtils.isEdge(north, east, south, west, center)) {
            cir.setReturnValue(InternalBiomeUtils.getRawId(InternalBiomeData.getOverworldEdges().get(centerBiome).pickRandom(context)));
        }
    }
}