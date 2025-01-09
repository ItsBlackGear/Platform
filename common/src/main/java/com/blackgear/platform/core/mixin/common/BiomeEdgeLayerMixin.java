package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.worldgen.BiomeSpawnPlacement;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.BiomeEdgeLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.IntConsumer;

@Mixin(BiomeEdgeLayer.class)
public class BiomeEdgeLayerMixin {
    @Inject(
        method = "apply",
        at = @At("HEAD"),
        cancellable = true
    )
    private void apply(Context context, int north, int east, int south, int west, int center, CallbackInfoReturnable<Integer> cir) {
        ResourceKey<Biome> biome = fromRawId(center);
        
        for (BiomeSpawnPlacement.PredicatedBiomeEntry entry : BiomeSpawnPlacement.getPredicatedBorders(biome)) {
            if (entry.predicate.test(fromRawId(north))
                || entry.predicate.test(fromRawId(south))
                || entry.predicate.test(fromRawId(west))
                || entry.predicate.test(fromRawId(east))
            ) {
                cir.setReturnValue(toRawId(entry.biome));
            }
        }
        
        //border biomes
        boolean replaced = tryReplace(center, north, cir::setReturnValue)
            || tryReplace(center, south, cir::setReturnValue)
            || tryReplace(center, west, cir::setReturnValue)
            || tryReplace(center, east, cir::setReturnValue);
        
        if (replaced) {
            return;
        }
    }
    
    @Unique
    private boolean tryReplace(int center, int neighbor, IntConsumer consumer) {
        if (center == neighbor) {
            return false;
        }
        
        Optional<ResourceKey<Biome>> border = BiomeSpawnPlacement.getBorder(fromRawId(neighbor));
        if (border.isPresent()) {
            consumer.accept(toRawId(border.get()));
            
            return true;
        }
        
        return false;
    }
    
    @Unique
    private ResourceKey<Biome> fromRawId(int center) {
        return Biomes.byId(center);
    }

    @Unique
    private int toRawId(ResourceKey<Biome> key) {
        return BuiltinRegistries.BIOME.getId(BuiltinRegistries.BIOME.get(key));
    }
}