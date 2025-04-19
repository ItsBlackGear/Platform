package com.blackgear.platform.common.block.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistryImpl {
    public static WoodType create(ResourceLocation location) {
        return WoodType.register(new WoodTypeImpl(location));
    }

    @Environment(EnvType.CLIENT)
    public static void registerWoodType(WoodType type) {
        Sheets.SIGN_MATERIALS.put(type, Sheets.createSignMaterial(type));
    }

    public static class WoodTypeImpl extends WoodType {
        private final ResourceLocation location;
        
        public WoodTypeImpl(ResourceLocation location) {
            super(location.getPath());
            this.location = location;
        }
        
        public ResourceLocation getLocation() {
            return this.location;
        }
    }
}