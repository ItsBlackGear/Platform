package com.blackgear.platform.core.mixin.core.biome;

import com.blackgear.platform.common.worldgen.biome.InternalBiomeUtils;
import com.blackgear.platform.common.worldgen.parameters.Temperature;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.BiomeInitLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.biome.Biomes.*;

@Mixin(BiomeInitLayer.class)
public class BiomeInitLayerMixin {
    @Shadow @Final private static int[] WARM_BIOMES;
    @Shadow @Final private static int[] MEDIUM_BIOMES;
    @Shadow @Final private static int[] COLD_BIOMES;
    @Shadow @Final private static int[] ICE_BIOMES;
    
    @Inject(
        method = "apply",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraftforge/common/BiomeManager$BiomeType;DESERT:Lnet/minecraftforge/common/BiomeManager$BiomeType;"
        ),
        cancellable = true
    )
    private void injectHotBiomes(Context context, int value, CallbackInfoReturnable<Integer> cir) {
        InternalBiomeUtils.injectBiomesIntoClimate(context, WARM_BIOMES, Temperature.HOT, cir::setReturnValue);
    }
    
    @Inject(
        method = "apply",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraftforge/common/BiomeManager$BiomeType;WARM:Lnet/minecraftforge/common/BiomeManager$BiomeType;"
        ),
        cancellable = true
    )
    private void injectWarmBiomes(Context context, int value, CallbackInfoReturnable<Integer> cir) {
        InternalBiomeUtils.injectBiomesIntoClimate(context, MEDIUM_BIOMES, Temperature.WARM, cir::setReturnValue);
    }
    
    @Inject(
        method = "apply",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraftforge/common/BiomeManager$BiomeType;COOL:Lnet/minecraftforge/common/BiomeManager$BiomeType;"
        ),
        cancellable = true
    )
    private void injectCoolBiomes(Context context, int value, CallbackInfoReturnable<Integer> cir) {
        InternalBiomeUtils.injectBiomesIntoClimate(context, COLD_BIOMES, Temperature.COOL, cir::setReturnValue);
    }
    
    @Inject(
        method = "apply",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraftforge/common/BiomeManager$BiomeType;ICY:Lnet/minecraftforge/common/BiomeManager$BiomeType;"
        ),
        cancellable = true
    )
    private void injectIcyBiomes(Context context, int value, CallbackInfoReturnable<Integer> cir) {
        InternalBiomeUtils.injectBiomesIntoClimate(context, ICE_BIOMES, Temperature.ICY, cir::setReturnValue);
    }
    
    @Inject(
        method = "apply",
        at = @At("RETURN"),
        cancellable = true
    )
    private void transformVariants(Context context, int value, CallbackInfoReturnable<Integer> cir) {
        int biomeId = cir.getReturnValueI();
        ResourceKey<Biome> biome = Biomes.byId(biomeId);
        
        // Determine what special case this is...
        Temperature climate = null;
        
        if (biome == BADLANDS_PLATEAU || biome == WOODED_BADLANDS_PLATEAU) {
            climate = Temperature.HOT;
        } else if (biome == JUNGLE) {
            climate = Temperature.WARM;
        } else if (biome == GIANT_TREE_TAIGA) {
            climate = Temperature.WARM;
        }
        
        cir.setReturnValue(InternalBiomeUtils.transformBiome(context, biome, climate));
    }
}