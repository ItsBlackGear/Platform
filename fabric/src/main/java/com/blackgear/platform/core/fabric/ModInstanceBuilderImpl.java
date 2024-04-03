package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.ModInstance;

public class ModInstanceBuilderImpl {
    public static ModInstance builder(String modId, Runnable common, Runnable postCommon, Runnable client, Runnable postClient) {
        return new ModInstance(modId, common, postCommon, client, postClient) {
            @Override public void bootstrap() {
                if (this.onCommon != null) this.onCommon.run();
                if (this.onPostCommon != null) this.onPostCommon.run();
                if (Environment.isClientSide()) {
                    if (this.onClient != null) this.onClient.run();
                    if (this.onPostClient != null) this.onPostClient.run();
                }
            }
        };
    }
}