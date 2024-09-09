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
            @Override public void bootstrap() {
                IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
                
                bus.<FMLCommonSetupEvent>addListener(event -> {
                    this.onPostCommon.accept(new ForgeParallelDispatch(event));
                });
                bus.<FMLClientSetupEvent>addListener(event -> {
                    this.onPostClient.accept(new ForgeParallelDispatch(event));
                });
                
                this.onCommon.run();
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.onClient.run());
            }
        };
    }
}
