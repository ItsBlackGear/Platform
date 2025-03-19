package com.blackgear.platform.core.helper;

import com.mojang.datafixers.types.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public record BlockEntityTypeBuilder<T extends BlockEntity>(Factory<? extends T> factory, List<Supplier<Block>> blocks) {
    public static <T extends BlockEntity> BlockEntityTypeBuilder<T> create(Factory<? extends T> factory, List<Supplier<Block>> blocks) {
        return new BlockEntityTypeBuilder<>(factory, blocks);
    }

    @SafeVarargs
    public static <T extends BlockEntity> BlockEntityTypeBuilder<T> create(Factory<? extends T> factory, Supplier<Block>... blocks) {
        List<Supplier<Block>> entries = new ArrayList<>(blocks.length);
        Collections.addAll(entries, blocks);
        return new BlockEntityTypeBuilder<>(factory, entries);
    }

    public BlockEntityTypeBuilder<T> add(Supplier<Block> block) {
        this.blocks.add(block);
        return this;
    }

    @SafeVarargs
    public final BlockEntityTypeBuilder<T> add(Supplier<Block>... block) {
        Collections.addAll(this.blocks, block);
        return this;
    }

    public BlockEntityType<T> build() {
        return build(null);
    }

    public BlockEntityType<T> build(Type<?> type) {
        return BlockEntityType.Builder.<T>of(this.factory::create, this.blocks.stream().map(Supplier::get).toArray(Block[]::new)).build(type);
    }

    public interface Factory<T extends BlockEntity> {
        T create(BlockPos origin, BlockState state);
    }
}