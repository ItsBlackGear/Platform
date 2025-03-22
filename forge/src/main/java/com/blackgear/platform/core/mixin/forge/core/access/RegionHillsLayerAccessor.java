package com.blackgear.platform.core.mixin.forge.core.access;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.level.newbiome.layer.RegionHillsLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegionHillsLayer.class)
public interface RegionHillsLayerAccessor {
    @Accessor
    static Int2IntMap getMUTATIONS() {
        throw new UnsupportedOperationException();
    }
}