package com.blackgear.platform.common.worldgen.decorator;

import com.blackgear.platform.common.worldgen.CaveSurface;
import com.blackgear.platform.common.worldgen.Column;
import com.blackgear.platform.core.mixin.access.DecorationContextAccessor;
import com.blackgear.platform.core.util.BlockUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Stream;

public class CaveSurfaceDecorator extends FeatureDecorator<CaveSurfaceConfiguration> {
    public CaveSurfaceDecorator(Codec<CaveSurfaceConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public Stream<BlockPos> getPositions(DecorationContext helper, Random random, CaveSurfaceConfiguration config, BlockPos origin) {
        Optional<Column> column = Column.scan(
            ((DecorationContextAccessor) helper).getLevel(),
            origin,
            config.floorToCeilingSearchRange,
            BlockBehaviour.BlockStateBase::isAir,
            state -> state.getMaterial().isSolid()
        );
        
        if (!column.isPresent()) {
            return Stream.empty();
        } else {
            OptionalInt surface = config.surface == CaveSurface.CEILING
                ? column.get().getCeiling()
                : column.get().getFloor();
            return !surface.isPresent()
                ? Stream.empty()
                : Stream.of(BlockUtils.atY(origin, surface.getAsInt() - config.surface.getY()));
        }
    }
}