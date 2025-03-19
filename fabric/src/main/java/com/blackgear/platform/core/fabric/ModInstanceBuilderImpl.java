package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.ModInstance;
import com.blackgear.platform.core.ParallelDispatch;

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
                try {
                    ParallelDispatch dispatch = new FabricParallelDispatch();

                    // Run common setup
                    this.onCommon.run();

                    // Run common post-setup
                    this.onPostCommon.accept(dispatch);

                    // Run client setup and post-setup if on client side
                    if (Environment.isClientSide()) {
                        this.onClient.run();
                        this.onPostClient.accept(dispatch);
                    }
                } catch (Exception exception) {
                    throw new RuntimeException("Failed to bootstrap mod: " + this.modId, exception);
                }
            }
        };
    }
}