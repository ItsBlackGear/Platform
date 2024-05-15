package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.ParallelDispatch;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FabricParallelDispatch implements ParallelDispatch {
    @Override
    public CompletableFuture<Void> enqueueWork(Runnable work) {
        work.run();
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
        return CompletableFuture.completedFuture(work.get());
    }
}
