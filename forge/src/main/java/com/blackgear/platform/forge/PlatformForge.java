package com.blackgear.platform.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.network.Networking;
import com.blackgear.platform.core.util.network.client.ClientNetworking;
import net.minecraftforge.fml.common.Mod;

@Mod(Platform.MOD_ID)
public class PlatformForge {
    public PlatformForge() {
        Platform.bootstrap();
        
        if (Environment.isClientSide()) {
            ClientNetworking.bootstrap();
        }
        
        Networking.bootstrap();
    }
}