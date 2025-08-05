package com.blackgear.platform.client.v2.emissive;

import org.jetbrains.annotations.Nullable;

public interface EmissiveModelWrapperHolder {
    @Nullable EmissiveModelWrapper getModelWrapper();

    void setModelWrapper(@Nullable EmissiveModelWrapper wrapper);
}