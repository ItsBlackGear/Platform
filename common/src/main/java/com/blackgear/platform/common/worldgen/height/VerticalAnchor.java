package com.blackgear.platform.common.worldgen.height;

import com.blackgear.platform.common.worldgen.WorldGenerationContext;
import com.blackgear.platform.core.util.ExtraCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.util.function.Function;

public abstract class VerticalAnchor {
    public static final Codec<VerticalAnchor> CODEC = ExtraCodecs.xor(
            Absolute.CODEC,
            ExtraCodecs.xor(
                AboveBottom.CODEC,
                BelowTop.CODEC
            )
        )
        .xmap(VerticalAnchor::merge, VerticalAnchor::split);
    private static final VerticalAnchor BOTTOM = aboveBottom(0);
    private static final VerticalAnchor TOP = belowTop(0);
    private final int value;
    
    protected VerticalAnchor(int value) {
        this.value = value;
    }
    
    public static VerticalAnchor absolute(int value) {
        return new Absolute(value);
    }
    
    public static VerticalAnchor aboveBottom(int value) {
        return new AboveBottom(value);
    }
    
    public static VerticalAnchor belowTop(int value) {
        return new BelowTop(value);
    }
    
    public static VerticalAnchor bottom() {
        return BOTTOM;
    }
    
    public static VerticalAnchor top() {
        return TOP;
    }
    
    private static VerticalAnchor merge(Either<Absolute, Either<AboveBottom, BelowTop>> either) {
        return either.map(Function.identity(), entry -> entry.map(Function.identity(), Function.identity()));
    }
    
    private static Either<Absolute, Either<AboveBottom, BelowTop>> split(VerticalAnchor anchor) {
        return anchor instanceof Absolute
            ? Either.left((Absolute) anchor)
            : Either.right(
            anchor instanceof AboveBottom
                ? Either.left((AboveBottom) anchor)
                : Either.right((BelowTop) anchor)
        );
    }
    
    protected int value() {
        return this.value;
    }
    
    public abstract int resolveY(WorldGenerationContext context);
    
    static final class AboveBottom extends VerticalAnchor {
        public static final Codec<AboveBottom> CODEC = Codec.intRange(-128, 512)
            .fieldOf("above_bottom")
            .xmap(AboveBottom::new, VerticalAnchor::value)
            .codec();
        
        private AboveBottom(int value) {
            super(value);
        }
        
        @Override
        public int resolveY(WorldGenerationContext context) {
            return context.getMinGenY() + this.value();
        }
        
        public String toString() {
            return this.value() + " above bottom";
        }
    }
    
    static final class Absolute extends VerticalAnchor {
        public static final Codec<Absolute> CODEC = Codec.intRange(-128, 512)
            .fieldOf("absolute")
            .xmap(Absolute::new, VerticalAnchor::value)
            .codec();
        
        private Absolute(int value) {
            super(value);
        }
        
        @Override
        public int resolveY(WorldGenerationContext context) {
            return this.value();
        }
        
        public String toString() {
            return this.value() + " absolute";
        }
    }
    
    static final class BelowTop extends VerticalAnchor {
        public static final Codec<BelowTop> CODEC = Codec.intRange(-128, 512)
            .fieldOf("below_top")
            .xmap(BelowTop::new, VerticalAnchor::value)
            .codec();
        
        private BelowTop(int value) {
            super(value);
        }
        
        @Override
        public int resolveY(WorldGenerationContext context) {
            return context.getGenDepth() - 1 + context.getMinGenY() - this.value();
        }
        
        public String toString() {
            return this.value() + " below top";
        }
    }
}