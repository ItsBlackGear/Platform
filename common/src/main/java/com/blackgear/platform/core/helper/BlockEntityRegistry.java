package com.blackgear.platform.core.helper;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class BlockEntityRegistry {
    private final CoreRegistry<BlockEntityType<?>> registry;

    private BlockEntityRegistry(String modId) {
        this.registry = CoreRegistry.create(Registry.BLOCK_ENTITY_TYPE, modId);
    }

    public static BlockEntityRegistry create(String modId) {
        return new BlockEntityRegistry(modId);
    }

    public <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String name, BlockEntityTypeBuilder<T> builder) {
        return this.registry.register(name, builder::build);
    }

    public void register() {
        this.registry.register();
    }
}