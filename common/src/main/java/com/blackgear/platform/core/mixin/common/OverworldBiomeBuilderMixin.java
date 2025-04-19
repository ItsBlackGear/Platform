package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.worldgen.placement.BiomePlacement;
import com.blackgear.platform.common.worldgen.placement.BiomeSpawnPlacement;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(OverworldBiomeBuilder.class)
public class OverworldBiomeBuilderMixin {
    @Inject(
        method = "addBiomes",
        at = @At("TAIL")
    )
    private void platform$addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, CallbackInfo ci) {
        BiomeSpawnPlacement.BIOME_ENTRIES.forEach(mapper);
        BiomePlacement.BIOME_PLACEMENTS.forEach(mapper);
    }
}