package com.blackgear.platform.core.mixin.access;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biomes.class)
public interface BiomesAccessor {
    @Accessor
    static Int2ObjectMap<ResourceKey<Biome>> getTO_NAME() {
        throw new UnsupportedOperationException();
    }
}