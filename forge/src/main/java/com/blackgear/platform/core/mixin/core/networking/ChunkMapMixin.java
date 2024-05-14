package com.blackgear.platform.core.mixin.core.networking;

import com.blackgear.platform.core.mixin.core.networking.access.TrackedEntityAccessor;
import com.blackgear.platform.core.util.network.ChunkMapExtensions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Collections;

@Mixin(ChunkMap.class)
public class ChunkMapMixin implements ChunkMapExtensions {
    @Shadow @Final private Int2ObjectMap<TrackedEntityAccessor> entityMap;
    
    @Override
    public Collection<ServerPlayer> getTrackingPlayers(Entity entity) {
        TrackedEntityAccessor access = this.entityMap.get(entity.getId());
        
        if (access != null) {
            return access.getSeenBy();
        }
        
        return Collections.emptySet();
    }
}