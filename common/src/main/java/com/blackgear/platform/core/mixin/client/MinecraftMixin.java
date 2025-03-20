package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.common.events.EntityEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.model.geom.EntityModelSet;
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
    @Shadow @Final private ReloadableResourceManager resourceManager;

    @Shadow @Nullable public HitResult hitResult;

    @Shadow @Nullable public LocalPlayer player;

    @Shadow @Nullable public MultiPlayerGameMode gameMode;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void platform$init(GameConfig gameConfig, CallbackInfo ci) {
        this.resourceManager.registerReloadListener(new EntityModelSet());
    }

    @Inject(
        method = "pickBlock",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$pickUpEntity(CallbackInfo ci) {
        if (this.hitResult != null && this.hitResult.getType() == HitResult.Type.ENTITY) {
            boolean isCreative = this.player.getAbilities().instabuild;
            final ItemStack[] pickResult = { ItemStack.EMPTY };

            if (!isCreative) return;

            Entity entity = ((EntityHitResult) this.hitResult).getEntity();

            EntityEvents.ON_PICK.invoker().onPickUp(entity, stack -> pickResult[0] = stack);

            if (!pickResult[0].isEmpty()) {
                Inventory inventory = this.player.getInventory();
                inventory.setPickedItem(pickResult[0]);
                this.gameMode.handleCreativeModeItemAdd(this.player.getItemInHand(InteractionHand.MAIN_HAND), 36 + inventory.selected);
                ci.cancel();
            }
        }
    }
}