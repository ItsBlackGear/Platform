package com.blackgear.platform.common.block.fabric;

import com.blackgear.platform.core.Environment;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistryImpl {
    public static WoodType create(ResourceLocation location) {
        WoodType type = WoodType.register(new WoodTypeImpl(location));
        if (Environment.isClientSide()) {
            Sheets.SIGN_MATERIALS.put(type, Sheets.createSignMaterial(type));
        }

        return type;
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