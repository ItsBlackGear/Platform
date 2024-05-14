package com.blackgear.platform.core;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface ParallelDispatch {
    CompletableFuture<Void> enqueueWork(Runnable work);
    
    <T> CompletableFuture<T> enqueueWork(Supplier<T> work);
}