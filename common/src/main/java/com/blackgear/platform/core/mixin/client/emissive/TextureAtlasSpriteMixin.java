package com.blackgear.platform.core.mixin.client.emissive;

import com.blackgear.platform.client.v2.emissive.EmissiveSpriteHolder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TextureAtlasSprite.class)
public class TextureAtlasSpriteMixin implements EmissiveSpriteHolder {
    @Unique private TextureAtlasSprite emissiveSprite;

    @Override
    public @Nullable TextureAtlasSprite getEmissiveSprite() {
        return this.emissiveSprite;
    }

    @Override
    public void setEmissiveSprite(TextureAtlasSprite sprite) {
        this.emissiveSprite = sprite;
    }
}