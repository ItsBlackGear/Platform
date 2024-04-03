package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.ModInstance;

public class ModInstanceBuilderImpl {
    public static ModInstance builder(String modId, Runnable common, Runnable postCommon, Runnable client, Runnable postClient) {
        return new ModInstance(modId, common, postCommon, client, postClient) {
            @Override public void bootstrap() {
                this.onCommon.run();
                this.onPostCommon.run();
                if (Environment.isClientSide()) {
                    this.onClient.run();
                    this.onPostClient.run();
                }
            }
        };
    }
}
