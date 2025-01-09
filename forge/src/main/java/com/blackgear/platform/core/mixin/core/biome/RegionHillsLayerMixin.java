package com.blackgear.platform.core.mixin.core.biome;

import com.blackgear.platform.common.worldgen.biome.InternalBiomeData;
import com.blackgear.platform.common.worldgen.biome.InternalBiomeUtils;
import com.blackgear.platform.common.worldgen.biome.WeightedBiomePicker;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.RegionHillsLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegionHillsLayer.class)
public class RegionHillsLayerMixin {
    @Shadow @Final private static Int2IntMap MUTATIONS;
    
    @Inject(
        method = "applyPixel",
        at = @At("HEAD"),
        cancellable = true
    )
    private void apply(Context context, Area biomeSampler, Area noiseSampler, int chunkX, int chunkZ, CallbackInfoReturnable<Integer> cir) {
        if (InternalBiomeData.getOverworldHills().isEmpty()) {
            // No use doing anything if there are no hills registered. Fall through to vanilla logic.
            return;
        }
        
        final int biomeId = biomeSampler.get(chunkX, chunkZ);
        int noiseSample = noiseSampler.get(chunkX, chunkZ);
        int processedNoiseSample = (noiseSample - 2) % 29;
        ResourceKey<Biome> key = Biomes.byId(biomeId);
        
        if (key == null) {
            throw new IllegalStateException("Biome sampler returned unregistered Biome ID: " + biomeId);
        }
        
        WeightedBiomePicker hillPicker = InternalBiomeData.getOverworldHills().get(key);
        
        if (hillPicker == null) {
            // No hills for this biome, fall through to vanilla logic.
            
            return;
        }
        
        if (context.nextRandom(3) == 0 || processedNoiseSample == 0) {
            int biomeReturn = InternalBiomeUtils.getRawId(hillPicker.pickRandom(context));
            
            if (processedNoiseSample == 0 && biomeReturn != biomeId) {
                biomeReturn = MUTATIONS.getOrDefault(biomeReturn, biomeId);
            }
            
            if (biomeReturn != biomeId) {
                int similarity = 0;
                
                if (Layers.isSame(biomeSampler.get(chunkX, chunkZ - 1), biomeId)) {
                    ++similarity;
                }
                
                if (Layers.isSame(biomeSampler.get(chunkX + 1, chunkZ), biomeId)) {
                    ++similarity;
                }
                
                if (Layers.isSame(biomeSampler.get(chunkX - 1, chunkZ), biomeId)) {
                    ++similarity;
                }
                
                if (Layers.isSame(biomeSampler.get(chunkX, chunkZ + 1), biomeId)) {
                    ++similarity;
                }
                
                if (similarity >= 3) {
                    cir.setReturnValue(biomeReturn);
                    return;
                }
            }
        }
        
        // Cancel vanilla logic.
        cir.setReturnValue(biomeId);
    }
}