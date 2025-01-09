package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.event.PickupEvent;
import com.blackgear.platform.client.renderer.model.geom.EntityModelSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Nullable public HitResult hitResult;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public ClientLevel level;
    @Shadow @Nullable public MultiPlayerGameMode gameMode;
    @Shadow @Final private ReloadableResourceManager resourceManager;
    
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void platform$init(CallbackInfo ci) {
        this.resourceManager.registerReloadListener(EntityModelSet.INSTANCE);
    }
    
    @Inject(
        method = "pickBlock",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$pickupEntity(CallbackInfo ci) {
        if (this.hitResult != null && this.hitResult.getType() == HitResult.Type.ENTITY) {
            boolean isCreative = this.player.abilities.instabuild;
            final ItemStack[] pickResult = { ItemStack.EMPTY };
            
            if (!isCreative) {
                return;
            }
            
            Entity entity = ((EntityHitResult) this.hitResult).getEntity();
            
            PickupEvent.Context context = new PickupEvent.Context() {
                @Override
                public Entity getEntity() {
                    return entity;
                }
                
                @Override
                public void setStack(ItemStack stack) {
                    pickResult[0] = stack;
                }
            };
            
            PickupEvent.PICK_ENTITY.invoker().pickEntity(context);
            
            if (!pickResult[0].isEmpty()) {
                Inventory inventory = this.player.inventory;
                
                inventory.setPickedItem(pickResult[0]);
                this.gameMode.handleCreativeModeItemAdd(this.player.getItemInHand(InteractionHand.MAIN_HAND), 36 + inventory.selected);
                
                ci.cancel();
            }
        }
    }
}