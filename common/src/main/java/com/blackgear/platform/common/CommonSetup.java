package com.blackgear.platform.common;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.events.DataLifecycleEvents;

public class CommonSetup {
    public static void setup() {
        DataLifecycleEvents.DATA_RELOAD.register(Platform::afterDataReload);
    }
}