package com.blackgear.platform.core.util;

import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

public class WorldGenUtils {
    public static int getUndergroundGenerationHeight(int seaLevel, int floorHeight) {
        floorHeight = floorHeight > seaLevel
            ? seaLevel + (floorHeight - seaLevel) / 2
            : floorHeight;
        return Mth.floor(floorHeight * 0.9F - 4);
    }

    public static int getUndergroundGenerationHeight(LevelAccessor level, Heightmap.Types type, int x, int z) {
        return getUndergroundGenerationHeight(level.getSeaLevel(), level.getHeight(type, x, z));
    }

    public static int getUndergroundGenerationHeight(LevelAccessor level, int x, int z) {
        return getUndergroundGenerationHeight(level, Heightmap.Types.OCEAN_FLOOR_WG, x, z);
    }
}