package com.blackgear.platform.client.renderer;

import com.blackgear.platform.client.renderer.model.NeoEntityModel;
import com.blackgear.platform.client.renderer.model.layer.NeoRenderLayer;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class NeoLivingEntityRenderer<T extends LivingEntity, M extends NeoEntityModel<T>> extends EntityRenderer<T> implements NeoRenderLayerParent<T, M> {
    protected M model;
    protected final List<NeoRenderLayer<T, M>> layers = Lists.newArrayList();
    
    protected NeoLivingEntityRenderer(EntityRenderDispatcher dispatcher, M model, float shadowRadius) {
        super(dispatcher);
        this.model = model;
        this.shadowRadius = shadowRadius;
    }
    
    protected final boolean addLayer(NeoRenderLayer<T, M> layer) {
        return this.layers.add(layer);
    }
    
    @Override
    public M getModel() {
        return this.model;
    }
    
    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        stack.pushPose();
        this.model.attackTime = this.getAttackAnim(entity, partialTicks);
        this.model.riding = entity.isPassenger();
        this.model.young = entity.isBaby();
        float bodyYRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headYRot = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float netHeadYaw = headYRot - bodyYRot;
        float ageInTicks;
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity vehicle = (LivingEntity) entity.getVehicle();
            bodyYRot = Mth.rotLerp(partialTicks, vehicle.yBodyRotO, vehicle.yBodyRot);
            netHeadYaw = headYRot - bodyYRot;
            ageInTicks = Mth.wrapDegrees(netHeadYaw);
            if (ageInTicks < -85.0F) {
                ageInTicks = -85.0F;
            }
            
            if (ageInTicks >= 85.0F) {
                ageInTicks = 85.0F;
            }
            
            bodyYRot = headYRot - ageInTicks;
            if (ageInTicks * ageInTicks > 2500.0F) {
                bodyYRot += ageInTicks * 0.2F;
            }
            
            netHeadYaw = headYRot - bodyYRot;
        }
        
        float headPitch = Mth.lerp(partialTicks, entity.xRotO, entity.xRot);
        if (entity.getPose() == Pose.SLEEPING) {
            Direction direction = entity.getBedOrientation();
            if (direction != null) {
                float eyeHeight = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                stack.translate((float) (-direction.getStepX()) * eyeHeight, 0.0, (float) (-direction.getStepZ()) * eyeHeight);
            }
        }
        
        ageInTicks = this.getBob(entity, partialTicks);
        this.setupRotations(entity, stack, ageInTicks, bodyYRot, partialTicks);
        stack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, stack, partialTicks);
        stack.translate(0.0, -1.501F, 0.0);
        float walkAnimationSpeed = 0.0F;
        float walkAnimationPos = 0.0F;
        if (!entity.isPassenger() && entity.isAlive()) {
            walkAnimationSpeed = Mth.lerp(partialTicks, entity.animationSpeedOld, entity.animationSpeed);
            walkAnimationPos = entity.animationPosition - entity.animationSpeed * (1.0F - partialTicks);
            if (entity.isBaby()) {
                walkAnimationPos *= 3.0F;
            }
            
            if (walkAnimationSpeed > 1.0F) {
                walkAnimationSpeed = 1.0F;
            }
        }
        
        this.model.prepareMobModel(entity, walkAnimationPos, walkAnimationSpeed, partialTicks);
        this.model.setupAnim(entity, walkAnimationPos, walkAnimationSpeed, ageInTicks, netHeadYaw, headPitch);
        Minecraft minecraft = Minecraft.getInstance();
        boolean bodyVisible = this.isBodyVisible(entity);
        boolean visibleToPlayer = !bodyVisible && !entity.isInvisibleTo(minecraft.player);
        boolean shouldGlow = minecraft.shouldEntityAppearGlowing(entity);
        
        RenderType renderType = this.getRenderType(entity, bodyVisible, visibleToPlayer, shouldGlow);
        if (renderType != null) {
            VertexConsumer vertex = buffer.getBuffer(renderType);
            int packedOverlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
            this.model.renderToBuffer(stack, vertex, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, visibleToPlayer ? 0.15F : 1.0F);
        }
        
        if (!entity.isSpectator()) {
            for (NeoRenderLayer<T, M> layer : this.layers) {
                layer.render(stack, buffer, packedLight, entity, walkAnimationPos, walkAnimationSpeed, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }
        
        stack.popPose();
        super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
    }
    
    @Nullable
    protected RenderType getRenderType(T entity, boolean isBodyVisible, boolean isVisibleToPlayer, boolean shouldGlow) {
        ResourceLocation texture = this.getTextureLocation(entity);
        
        if (isVisibleToPlayer) {
            return RenderType.itemEntityTranslucentCull(texture);
        } else if (isBodyVisible) {
            return this.model.renderType(texture);
        } else {
            return shouldGlow ? RenderType.outline(texture) : null;
        }
    }
    
    public static int getOverlayCoords(LivingEntity entity, float progress) {
        return OverlayTexture.pack(OverlayTexture.u(progress), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
    }
    
    protected boolean isBodyVisible(T livingEntity) {
        return !livingEntity.isInvisible();
    }
    
    private static float sleepDirectionToRotation(Direction direction) {
        switch (direction) {
            case SOUTH:
                return 90.0F;
            case NORTH:
                return 270.0F;
            case EAST:
                return 180.0F;
            default:
                return 0.0F;
        }
    }
    
    protected boolean isShaking(T entity) {
        return false;
    }
    
    protected void setupRotations(T entity, PoseStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.isShaking(entity)) {
            rotationYaw += (float) (Math.cos((double) entity.tickCount * 3.25) * Math.PI * 0.4F);
        }
        
        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING) {
            stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
        }
        
        if (entity.deathTime > 0) {
            float deathAnimationProgress = ((float) entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            deathAnimationProgress = Mth.sqrt(deathAnimationProgress);
            if (deathAnimationProgress > 1.0F) {
                deathAnimationProgress = 1.0F;
            }
            
            stack.mulPose(Vector3f.ZP.rotationDegrees(deathAnimationProgress * this.getFlipDegrees(entity)));
        } else if (entity.isAutoSpinAttack()) {
            stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F - entity.xRot));
            stack.mulPose(Vector3f.YP.rotationDegrees(((float) entity.tickCount + partialTicks) * -75.0F));
        } else if (pose == Pose.SLEEPING) {
            Direction bedOrientation = entity.getBedOrientation();
            float rotation = bedOrientation != null ? sleepDirectionToRotation(bedOrientation) : rotationYaw;
            stack.mulPose(Vector3f.YP.rotationDegrees(rotation));
            stack.mulPose(Vector3f.ZP.rotationDegrees(this.getFlipDegrees(entity)));
            stack.mulPose(Vector3f.YP.rotationDegrees(270.0F));
        } else if (entity.hasCustomName() || entity instanceof Player) {
            String name = ChatFormatting.stripFormatting(entity.getName().getString());
            if (
                ("Dinnerbone".equals(name) || "Grumm".equals(name))
                && (!(entity instanceof Player) || ((Player) entity).isModelPartShown(PlayerModelPart.CAPE))
            ) {
                stack.translate(0.0, entity.getBbHeight() + 0.1F, 0.0);
                stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            }
        }
    }
    
    protected float getAttackAnim(T entity, float partialTick) {
        return entity.getAttackAnim(partialTick);
    }
    
    protected float getBob(T entity, float partialTick) {
        return (float)entity.tickCount + partialTick;
    }
    
    protected float getFlipDegrees(T entity) {
        return 90.0F;
    }
    
    protected float getWhiteOverlayProgress(T entity, float partialTick) {
        return 0.0F;
    }
    
    protected void scale(T entity, PoseStack stack, float partialTick) {
    }
    
    protected boolean shouldShowName(T entity) {
        double distance = this.entityRenderDispatcher.distanceToSqr(entity);
        float range = entity.isDiscrete() ? 32.0F : 64.0F;
        
        if (distance >= (double) (range * range)) {
            return false;
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            boolean isVisibleToPlayer = !entity.isInvisibleTo(player);
            if (entity != player) {
                Team entityTeam = entity.getTeam();
                Team playerTeam = player.getTeam();
                if (entityTeam != null) {
                    Team.Visibility visibility = entityTeam.getNameTagVisibility();
                    
                    switch(visibility) {
                        case ALWAYS:
                            return isVisibleToPlayer;
                        case NEVER:
                            return false;
                        case HIDE_FOR_OTHER_TEAMS:
                            return playerTeam == null
                                ? isVisibleToPlayer
                                : entityTeam.isAlliedTo(playerTeam) && (entityTeam.canSeeFriendlyInvisibles() || isVisibleToPlayer);
                        case HIDE_FOR_OWN_TEAM:
                            return playerTeam == null
                                ? isVisibleToPlayer
                                : !entityTeam.isAlliedTo(playerTeam) && isVisibleToPlayer;
                        default:
                            return true;
                    }
                }
            }
            
            return Minecraft.renderNames() && entity != minecraft.getCameraEntity() && isVisibleToPlayer && !entity.isVehicle();
        }
    }
}