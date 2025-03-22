package com.blackgear.platform.core.mixin.forge.core.biome;

import com.blackgear.platform.common.worldgen.biome.InternalBiomeData;
import com.blackgear.platform.common.worldgen.biome.InternalBiomeUtils;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.RiverMixerLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(RiverMixerLayer.class)
public class RiverMixerLayerMixin {
    @Inject(
        method = "applyPixel",
        at = @At("HEAD"),
        cancellable = true
    )
    private void apply(Context context, Area landSampler, Area riverSampler, int x, int z, CallbackInfoReturnable<Integer> cir) {
        int landBiomeId = landSampler.get(x, z);
        ResourceKey<Biome> landBiomeKey = Biomes.byId(landBiomeId);
        
        int riverBiomeId = riverSampler.get(x, z);
        Map<ResourceKey<Biome>, ResourceKey<Biome>> overworldRivers = InternalBiomeData.getOverworldRivers();
        
        if (overworldRivers.containsKey(landBiomeKey) && Biomes.byId(riverBiomeId) == net.minecraft.world.level.biome.Biomes.RIVER) {
            ResourceKey<Biome> riverBiome = overworldRivers.get(landBiomeKey);
            cir.setReturnValue(riverBiome == null ? landBiomeId : InternalBiomeUtils.getRawId(riverBiome));
        }
    }
}