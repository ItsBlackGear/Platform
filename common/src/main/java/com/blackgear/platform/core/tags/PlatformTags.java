package com.blackgear.platform.core.tags;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.TagRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class PlatformTags {
    public static final TagRegistry TAGS = TagRegistry.of(Platform.MOD_ID);
    
    public static final Tag.Named<Block> DIRT = TAGS.blocks("dirt");
}