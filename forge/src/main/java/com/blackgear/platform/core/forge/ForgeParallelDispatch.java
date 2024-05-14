package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.ParallelDispatch;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ForgeParallelDispatch implements ParallelDispatch {
    private final ParallelDispatchEvent event;
    
    public ForgeParallelDispatch(ParallelDispatchEvent event) {
        this.event = event;
    }
    
    @Override
    public CompletableFuture<Void> enqueueWork(Runnable work) {
        return this.event.enqueueWork(work);
    }
    
    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
        return this.event.enqueueWork(work);
    }
}
