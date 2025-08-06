package com.blackgear.platform.client;

import com.blackgear.platform.client.v2.emissive.EmissiveModelWrapper;

public class ClientSetup {
    public static void setup() {
        EmissiveModelWrapper.bootstrap();
    }
}