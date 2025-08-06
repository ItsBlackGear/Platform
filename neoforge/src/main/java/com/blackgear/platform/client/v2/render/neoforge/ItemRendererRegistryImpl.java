package com.blackgear.platform.client.v2.render.neoforge;

import com.blackgear.platform.client.v2.render.ItemRendererRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemRendererRegistryImpl {
    public static void registerRenderer(ItemLike item, ItemRendererRegistry.DynamicItemRenderer renderer) {
        Consumer<RegisterClientExtensionsEvent> consumer = event -> event.registerItem(new RenderPropertiesWrapper(IClientItemExtensions.of(item.asItem()), renderer), item.asItem());
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener(consumer);
//        ((ItemAccessor) item.asItem()).setRenderProperties(
//            new RenderPropertiesWrapper(IClientItemExtensions.of(item.asItem()), renderer)
//        );
    }

    private static class RenderPropertiesWrapper implements IClientItemExtensions {
        private final Minecraft minecraft = Minecraft.getInstance();
        private final IClientItemExtensions parent;
        private final ItemRendererRegistry.DynamicItemRenderer renderer;

        private RenderPropertiesWrapper(@Nullable IClientItemExtensions parent, ItemRendererRegistry.DynamicItemRenderer renderer) {
            this.parent = parent != null ? parent : IClientItemExtensions.DEFAULT;
            this.renderer = renderer;
        }

        @Override
        public @Nullable Font getFont(ItemStack stack, FontContext context) {
            return this.parent.getFont(stack, context);
        }

        @Override
        public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
            return this.parent.getHumanoidArmorModel(entity, stack, slot, original);
        }

        @Override
        public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
            this.parent.renderHelmetOverlay(stack, player, width, height, partialTick);
        }

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return new BlockEntityWithoutLevelRenderer(this.minecraft.getBlockEntityRenderDispatcher(), this.minecraft.getEntityModels()) {
                @Override
                public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
                    renderer.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
                }
            };
        }
    }
}