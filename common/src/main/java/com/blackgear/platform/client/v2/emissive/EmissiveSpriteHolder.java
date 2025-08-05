package com.blackgear.platform.client.v2.emissive;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

public interface EmissiveSpriteHolder {
    @Nullable TextureAtlasSprite getEmissiveSprite();

    void setEmissiveSprite(TextureAtlasSprite sprite);
}