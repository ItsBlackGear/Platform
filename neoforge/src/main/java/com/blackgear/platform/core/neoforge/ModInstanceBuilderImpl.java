package com.blackgear.platform.core.neoforge;

import com.blackgear.platform.core.ModInstance;
import com.blackgear.platform.core.ParallelDispatch;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

public class ModInstanceBuilderImpl {
    public static ModInstance builder(
        String modId,
        Runnable common,
        Consumer<ParallelDispatch> postCommon,
        Runnable client,
        Consumer<ParallelDispatch> postClient
    ) {
        return new ModInstance(modId, common, postCommon, client, postClient) {
            @Override public void bootstrap() {
                IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
                if (bus == null) {
                    throw new IllegalStateException("Failed to get Forge mod event bus");
                }

                // Register common post-setup
                bus.<FMLCommonSetupEvent>addListener(event -> {
                    this.onPostCommon.accept(new ForgeParallelDispatch(event));
                });

                // Register client post-setup (will only be called on client)
                bus.<FMLClientSetupEvent>addListener(event -> {
                    this.onPostClient.accept(new ForgeParallelDispatch(event));
                });

                // Run common setup immediately
                this.onCommon.run();

                // Run client setup immediately if on client side
                if (FMLEnvironment.dist.isClient()) {
                    this.onClient.run();
                }
            }
        };
    }
}