package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.ModInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModInstanceBuilderImpl {
    public static ModInstance builder(String modId, Runnable common, Runnable postCommon, Runnable client, Runnable postClient) {
        return new ModInstance(modId, common, postCommon, client, postClient) {
            @Override public void bootstrap() {
                IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
                if (this.onPostCommon != null) bus.<FMLCommonSetupEvent>addListener(event -> this.onPostCommon.run());
                if (this.onPostClient != null) bus.<FMLClientSetupEvent>addListener(event -> this.onPostClient.run());
                if (this.onCommon != null) this.onCommon.run();
                if (this.onClient != null) DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> this.onClient.run());
            }
        };
    }
}
