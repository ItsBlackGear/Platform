package com.blackgear.platform.client.v2.emissive;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class Emissiveness {
    public static final Emissiveness INSTANCE = new Emissiveness();

    public TextureAtlasSprite getEmissiveSprite(TextureAtlasSprite sprite) {
        return ((EmissiveSpriteHolder) sprite).getEmissiveSprite();
    }
}