package com.blackgear.platform.core.mixin.core;

import com.blackgear.platform.common.data.LootModifierEvent;
import com.blackgear.platform.common.data.LootRegistry;
import com.blackgear.platform.common.data.loot.LootTableBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mixin(LootTables.class)
public class LootTablesMixin {
    @Shadow private Map<ResourceLocation, LootTable> tables;

    @Inject(
        method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At("RETURN")
    )
    private void apply(
        Map<ResourceLocation, JsonElement> object,
        ResourceManager resourceManager,
        ProfilerFiller profiler,
        CallbackInfo ci
    ) {
        Map<ResourceLocation, LootTable> tables = new HashMap<>();

        this.tables.forEach((path, lootTable) -> {
            LootTableBuilder builder = LootTableBuilder.of(lootTable);

            LootModifierEvent.EVENT.invoker().modify(
                (LootTables) (Object) this,
                path,
                new LootModifierEvent.Adder() {
                    @Override
                    public void addPool(LootPool pool) {
                        builder.withPool(pool);
                    }

                    @Override
                    public void addPool(LootPool.Builder pool) {
                        builder.withPool(pool);
                    }
                },
                new LootModifierEvent.Setter() {
                    @Override
                    public void setTable(LootTable lootTable) {
                        tables.put(path, lootTable);
                    }

                    @Override
                    public void setBlock(Block block, Function<Block, LootTable.Builder> factory) {
                        if (path.equals(block.getLootTable())) {
                            this.setTable(factory.apply(block).build());
                        }
                    }
                }
            );

            tables.computeIfAbsent(path, key -> builder.build());
        });

        this.tables = ImmutableMap.copyOf(tables);
    }
}