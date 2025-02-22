package com.blackgear.platform.core.helper;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

import java.util.function.Supplier;

public class SoundRegistry {
    private final CoreRegistry<SoundEvent> sounds;
    private final String modId;

    public static SoundRegistry create(String modId) {
        return new SoundRegistry(modId);
    }

    private SoundRegistry(String modId) {
        this.modId = modId;
        this.sounds = CoreRegistry.create(Registry.SOUND_EVENT, this.modId);
    }

    public Supplier<SoundEvent> soundEvent(String name) {
        return this.sounds.register(name, () -> new SoundEvent(new ResourceLocation(this.modId, name)));
    }

    public SoundType soundType(
        float volume,
        float pitch,
        Supplier<SoundEvent> breakSound,
        Supplier<SoundEvent> stepSound,
        Supplier<SoundEvent> placeSound,
        Supplier<SoundEvent> hitSound,
        Supplier<SoundEvent> fallSound
    ) {
        return new SoundTypeImpl(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    public SoundType soundType(
        Supplier<SoundEvent> breakSound,
        Supplier<SoundEvent> stepSound,
        Supplier<SoundEvent> placeSound,
        Supplier<SoundEvent> hitSound,
        Supplier<SoundEvent> fallSound
    ) {
        return soundType(1.0F, 1.0F, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    public void register() {
        this.sounds.register();
    }

    static class SoundTypeImpl extends SoundType {
        private final Supplier<SoundEvent> breakSound;
        private final Supplier<SoundEvent> stepSound;
        private final Supplier<SoundEvent> placeSound;
        private final Supplier<SoundEvent> hitSound;
        private final Supplier<SoundEvent> fallSound;

        public SoundTypeImpl(
            float volume,
            float pitch,
            Supplier<SoundEvent> breakSound,
            Supplier<SoundEvent> stepSound,
            Supplier<SoundEvent> placeSound,
            Supplier<SoundEvent> hitSound,
            Supplier<SoundEvent> fallSound
        ) {
            super(volume, pitch, null, null, null, null, null);
            this.breakSound = breakSound;
            this.stepSound = stepSound;
            this.placeSound = placeSound;
            this.hitSound = hitSound;
            this.fallSound = fallSound;
        }

        @Override
        public SoundEvent getBreakSound() {
            return this.breakSound.get();
        }

        @Override
        public SoundEvent getStepSound() {
            return this.stepSound.get();
        }

        @Override
        public SoundEvent getPlaceSound() {
            return this.placeSound.get();
        }

        @Override
        public SoundEvent getHitSound() {
            return this.hitSound.get();
        }

        @Override
        public SoundEvent getFallSound() {
            return this.fallSound.get();
        }
    }
}