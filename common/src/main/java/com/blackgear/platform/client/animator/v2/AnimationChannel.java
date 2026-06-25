package com.blackgear.platform.client.animator.v2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public record AnimationChannel(Target target, Keyframe... keyframes) {
    @Environment(EnvType.CLIENT)
    public interface Interpolation {
        Vector3f apply(Vector3f result, Query query, float progress, Keyframe[] keyframes, int startIndex, int endIndex, float scale);
    }

    @Environment(EnvType.CLIENT)
    public static class Interpolations {
        public static final Interpolation LINEAR = (result, query, progress, keyframes, startIndex, endIndex, scale) -> {
            Vector3f startTarget = keyframes[startIndex].target().resolve(query);
            Vector3f endTarget = keyframes[endIndex].target().resolve(query);
            return startTarget.lerp(endTarget, progress, result).mul(scale);
        };
        public static final Interpolation CATMULLROM = (result, query, progress, keyframes, startIndex, endIndex, scale) -> {
            Vector3f beforeStart = keyframes[Math.max(0, startIndex - 1)].target().resolve(query);
            Vector3f start = keyframes[startIndex].target().resolve(query);
            Vector3f end = keyframes[endIndex].target().resolve(query);
            Vector3f afterEnd = keyframes[Math.min(keyframes.length - 1, endIndex + 1)].target().resolve(query);
            result.set(
                Mth.catmullrom(progress, beforeStart.x(), start.x(), end.x(), afterEnd.x()) * scale,
                Mth.catmullrom(progress, beforeStart.y(), start.y(), end.y(), afterEnd.y()) * scale,
                Mth.catmullrom(progress, beforeStart.z(), start.z(), end.z(), afterEnd.z()) * scale
            );
            return result;
        };
    }

    @Environment(EnvType.CLIENT)
    public interface Target {
        void apply(ModelPart modelPart, Vector3f value);
    }

    @Environment(EnvType.CLIENT)
    public static class Targets {
        public static final Target POSITION = ModelPart::offsetPos;
        public static final Target ROTATION = ModelPart::offsetRotation;
        public static final Target SCALE = ModelPart::offsetScale;
    }

    @Environment(EnvType.CLIENT)
    public interface Value {
        Vector3f resolve(Query query);
    }

    @Environment(EnvType.CLIENT)
    public interface Query {
        float animTime();
    }
}