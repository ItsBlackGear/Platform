package com.blackgear.platform.common.worldgen.placement;

import com.blackgear.platform.common.worldgen.placement.parameters.Weirdness;

public enum Placement {
    VALLEY(Weirdness.VALLEY),
    LOW_SLICE(Weirdness.LOW_SLICE_NORMAL_DESCENDING, Weirdness.LOW_SLICE_VARIANT_ASCENDING),
    LOW_SLICE_NORMAL(Weirdness.LOW_SLICE_NORMAL_DESCENDING),
    LOW_SLICE_VARIANT(Weirdness.LOW_SLICE_VARIANT_ASCENDING),
    MID_SLICE(Weirdness.MID_SLICE_NORMAL_ASCENDING, Weirdness.MID_SLICE_VARIANT_ASCENDING, Weirdness.MID_SLICE_NORMAL_DESCENDING, Weirdness.MID_SLICE_VARIANT_DESCENDING),
    MID_SLICE_NORMAL(Weirdness.MID_SLICE_NORMAL_ASCENDING, Weirdness.MID_SLICE_NORMAL_DESCENDING),
    MID_SLICE_VARIANT(Weirdness.MID_SLICE_VARIANT_ASCENDING, Weirdness.MID_SLICE_VARIANT_DESCENDING),
    HIGH_SLICE(Weirdness.HIGH_SLICE_NORMAL_ASCENDING, Weirdness.HIGH_SLICE_VARIANT_ASCENDING, Weirdness.HIGH_SLICE_NORMAL_DESCENDING, Weirdness.HIGH_SLICE_VARIANT_DESCENDING),
    HIGH_SLICE_NORMAL(Weirdness.HIGH_SLICE_NORMAL_ASCENDING, Weirdness.HIGH_SLICE_NORMAL_DESCENDING),
    HIGH_SLICE_VARIANT(Weirdness.HIGH_SLICE_VARIANT_ASCENDING, Weirdness.HIGH_SLICE_VARIANT_DESCENDING),
    PEAK(Weirdness.PEAK_NORMAL, Weirdness.PEAK_VARIANT),
    PEAK_NORMAL(Weirdness.PEAK_NORMAL),
    PEAK_VARIANT(Weirdness.PEAK_VARIANT);

    private final Weirdness[] weirdnesses;

    Placement(Weirdness... weirdnesses) {
        this.weirdnesses = weirdnesses;
    }

    public Weirdness[] getWeirdnesses() {
        return this.weirdnesses;
    }
}