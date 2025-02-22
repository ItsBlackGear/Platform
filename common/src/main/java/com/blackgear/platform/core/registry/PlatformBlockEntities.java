package com.blackgear.platform.core.registry;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.helper.SkullRegistry;
import com.blackgear.platform.common.blockentity.PlatformSkullBlockEntity;
import com.blackgear.platform.core.helper.BlockEntityRegistry;
import com.blackgear.platform.core.helper.BlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class PlatformBlockEntities {
    public static final BlockEntityRegistry BLOCK_ENTITIES = BlockEntityRegistry.create(Platform.MOD_ID);

    public static final Supplier<BlockEntityType<PlatformSkullBlockEntity>> SKULL = BLOCK_ENTITIES.register(
        "skull",
        BlockEntityTypeBuilder.create(
            PlatformSkullBlockEntity::new,
            SkullRegistry.SKULLS
        )
    );
}