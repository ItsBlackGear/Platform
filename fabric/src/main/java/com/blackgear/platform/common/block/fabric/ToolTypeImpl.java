package com.blackgear.platform.common.block.fabric;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ToolTypeImpl {
    public static Tag<Item> pickaxe() {
        return FabricToolTags.PICKAXES;
    }
    
    public static Tag<Item> axe() {
        return FabricToolTags.AXES;
    }
    
    public static Tag<Item> hoe() {
        return FabricToolTags.HOES;
    }
    
    public static Tag<Item> shovel() {
        return FabricToolTags.SHOVELS;
    }
}