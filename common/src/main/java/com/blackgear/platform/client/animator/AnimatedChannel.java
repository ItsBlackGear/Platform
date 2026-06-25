package com.blackgear.platform.client.animator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT) @Deprecated
public record AnimatedChannel(AnimatedPoint... targets) {}