package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.Platform;
import net.minecraftforge.fml.common.Mod;

@Mod(Platform.MOD_ID)
public class PlatformForge {
    public PlatformForge() {
        Platform.bootstrap();
    }
}