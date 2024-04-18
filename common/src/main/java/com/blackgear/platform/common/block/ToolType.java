package com.blackgear.platform.common.block;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public enum ToolType {
    PICKAXE("pickaxe", ToolType::pickaxe),
    AXE("axe", ToolType::axe),
    HOE("hoe", ToolType::hoe),
    SHOVEL("shovel", ToolType::shovel);
    
    public final String name;
    public final Supplier<Tag<Item>> tag;
    
    ToolType(String name, Supplier<Tag<Item>> tag) {
        this.name = name;
        this.tag = tag;
    }
    
    @ExpectPlatform
    public static Tag<Item> pickaxe() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    private static Tag<Item> axe() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    private static Tag<Item> hoe() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    private static Tag<Item> shovel() {
        throw new AssertionError();
    }
}