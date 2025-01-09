package com.blackgear.platform.client.renderer.model.animation;

import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import com.blackgear.platform.core.util.MathUtils;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;

public class AnimationChannel {
    private final Target target;
    private final Keyframe[] keyframes;
    
    public AnimationChannel(Target target, Keyframe... keyframes) {
        this.target = target;
        this.keyframes = keyframes;
    }
    
    public Target target() {
        return this.target;
    }
    
    public Keyframe[] keyframes() {
        return this.keyframes;
    }
    
    @Environment(EnvType.CLIENT)
    public interface Target {
        void apply(NeoModelPart modelPart, Vector3f vector3f);
    }
    
    @Environment(EnvType.CLIENT)
    public static class Interpolations {
        public static final Interpolation LINEAR = (cache, delta, keyframes, start, end, speed) -> {
            Vector3f frameStart = keyframes[start].target();
            Vector3f frameEnd = keyframes[end].target();
            cache.set(
                Mth.lerp(delta, frameStart.x(), frameEnd.x()) * speed,
                Mth.lerp(delta, frameStart.y(), frameEnd.y()) * speed,
                Mth.lerp(delta, frameStart.z(), frameEnd.z()) * speed
            );
            return cache;
        };
        public static final Interpolation CATMULLROM = (cache, delta, keyframes, start, end, speed) -> {
            Vector3f frameStartPoint = keyframes[Math.max(0, start - 1)].target();
            Vector3f frameStart = keyframes[start].target();
            Vector3f frameEnd = keyframes[end].target();
            Vector3f frameEndPoint = keyframes[Math.min(keyframes.length - 1, end + 1)].target();
            cache.set(
                MathUtils.catmullrom(delta, frameStartPoint.x(), frameStart.x(), frameEnd.x(), frameEndPoint.x()) * speed,
                MathUtils.catmullrom(delta, frameStartPoint.y(), frameStart.y(), frameEnd.y(), frameEndPoint.y()) * speed,
                MathUtils.catmullrom(delta, frameStartPoint.z(), frameStart.z(), frameEnd.z(), frameEndPoint.z()) * speed
            );
            return cache;
        };
    }
    
    @Environment(EnvType.CLIENT)
    public static class Targets {
        public static final Target POSITION = NeoModelPart::offsetPos;
        public static final Target ROTATION = NeoModelPart::offsetRotation;
        public static final Target SCALE = NeoModelPart::offsetScale;
    }
    
    @Environment(EnvType.CLIENT)
    public interface Interpolation {
        Vector3f apply(Vector3f cache, float delta, Keyframe[] keyframes, int start, int end, float speed);
    }
}