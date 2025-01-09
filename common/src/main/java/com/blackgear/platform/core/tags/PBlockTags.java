package com.blackgear.platform.core.tags;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.TagRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class PBlockTags {
    public static final TagRegistry TAGS = TagRegistry.of(Platform.MOD_ID);
    
    public static final Tag.Named<Block> DIRT = TAGS.blocks("dirt");
    public static final Tag.Named<Block> CARVERS_CAN_REPLACE = TAGS.blocks("carvers_can_replace");
    public static final Tag.Named<Block> FEATURES_CANNOT_REPLACE = TAGS.blocks("features_cannot_replace");
    public static final Tag.Named<Block> STONE_ORE_REPLACEABLES = TAGS.blocks("stone_ore_replaceables");
}