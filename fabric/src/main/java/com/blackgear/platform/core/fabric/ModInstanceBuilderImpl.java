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
                ParallelDispatch dispatch = new FabricParallelDispatch();
                
                this.onCommon.run();
                this.onPostCommon.accept(dispatch);
                if (Environment.isClientSide()) {
                    this.onClient.run();
                    this.onPostClient.accept(dispatch);
                }
            }
        };
    }
}