package com.blackgear.platform.common.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class TagRegistry {
    private final String modId;
    
    private TagRegistry(String modId) {
        this.modId = modId;
    }
    
    public static TagRegistry of(String modId) {
        return new TagRegistry(modId);
    }
    
    public void instance() {}
    
    public Tag.Named<Block> blocks(String name) {
        return BlockTags.bind(new ResourceLocation(this.modId, name).toString());
    }
    
    public Tag.Named<EntityType<?>> entities(String name) {
        return EntityTypeTags.bind(new ResourceLocation(this.modId, name).toString());
    }
    
    public Tag.Named<Fluid> fluids(String name) {
        return FluidTags.bind(new ResourceLocation(this.modId, name).toString());
    }
    
    public Tag.Named<Item> items(String name) {
        return ItemTags.bind(new ResourceLocation(this.modId, name).toString());
    }
}