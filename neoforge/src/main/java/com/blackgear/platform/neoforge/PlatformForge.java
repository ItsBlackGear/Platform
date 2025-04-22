package com.blackgear.platform.neoforge;

import com.blackgear.platform.Platform;
import net.neoforged.fml.common.Mod;

@Mod(Platform.MOD_ID)
public final class PlatformForge {
    public PlatformForge() {
        // Run our common setup.
        Platform.bootstrap();
    }
}
