package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.ModInstance;
import com.blackgear.platform.core.ParallelDispatch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
            @Override
            public void bootstrap() {
                try {
                    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
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
                    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> this.onClient.run());
                } catch (Exception exception) {
                    throw new RuntimeException("Failed to bootstrap mod: " + this.modId, exception);
                }
            }
        };
    }
}
