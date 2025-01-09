package com.blackgear.platform.client.renderer;


import com.blackgear.platform.client.renderer.model.NeoEntityModel;
import com.blackgear.platform.core.mixin.access.EntityRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public abstract class NeoMobRenderer<T extends Mob, M extends NeoEntityModel<T>> extends NeoLivingEntityRenderer<T, M> {
    public NeoMobRenderer(EntityRenderDispatcher dispatcher, M model, float shadowRadius) {
        super(dispatcher, model, shadowRadius);
    }
    
    protected boolean shouldShowName(T entity) {
        return super.shouldShowName(entity) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }
    
    public boolean shouldRender(T mob, Frustum frustum, double camX, double camY, double camZ) {
        if (super.shouldRender(mob, frustum, camX, camY, camZ)) {
            return true;
        } else {
            Entity entity = mob.getLeashHolder();
            return entity != null && frustum.isVisible(entity.getBoundingBoxForCulling());
        }
    }
    
    public void render(T entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
        Entity leashHolder = entity.getLeashHolder();
        
        if (leashHolder != null) {
            this.renderLeashLegacy(entity, partialTicks, stack, buffer, leashHolder);
        }
    }
    
    private <E extends Entity> void renderLeash(T entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, E leashHolder) {
        stack.pushPose();
        Vec3 ropeHoldPosition = leashHolder.getRopeHoldPosition(partialTicks);
        double bodyRotation = (double) (Mth.lerp(partialTicks, entity.yBodyRot, entity.yBodyRotO) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
        Vec3 leashOffset = entity.getLeashOffset();
        double leashOffsetX = Math.cos(bodyRotation) * leashOffset.z + Math.sin(bodyRotation) * leashOffset.x;
        double leashOffsetZ = Math.sin(bodyRotation) * leashOffset.z - Math.cos(bodyRotation) * leashOffset.x;
        double entityX = Mth.lerp(partialTicks, entity.xo, entity.getX()) + leashOffsetX;
        double entityY = Mth.lerp(partialTicks, entity.yo, entity.getY()) + leashOffset.y;
        double entityZ = Mth.lerp(partialTicks, entity.zo, entity.getZ()) + leashOffsetZ;
        stack.translate(leashOffsetX, leashOffset.y, leashOffsetZ);
        float deltaX = (float) (ropeHoldPosition.x - entityX);
        float deltaY = (float) (ropeHoldPosition.y - entityY);
        float deltaZ = (float) (ropeHoldPosition.z - entityZ);
        VertexConsumer vertex = buffer.getBuffer(RenderType.leash());
        Matrix4f matrix = stack.last().pose();
        float radius = Mth.fastInvSqrt(deltaX * deltaX + deltaZ * deltaZ) * 0.025F / 2.0F;
        float xOffset = deltaZ * radius;
        float zOffset = deltaX * radius;
        BlockPos entityPosition = new BlockPos(entity.getEyePosition(partialTicks));
        BlockPos leashHolderPosition = new BlockPos(leashHolder.getEyePosition(partialTicks));
        int entityBlockLight = this.getBlockLightLevel(entity, entityPosition);
        int leashHolderBlockLight = ((EntityRendererAccessor) this.entityRenderDispatcher.getRenderer(leashHolder)).callGetBlockLightLevel(leashHolder, leashHolderPosition);
        int entitySkyLight = entity.level.getBrightness(LightLayer.SKY, entityPosition);
        int leashHolderSkyLight = entity.level.getBrightness(LightLayer.SKY, leashHolderPosition);

        for(int segments = 0; segments <= 24; ++segments) {
            addVertexPair(vertex, matrix, deltaX, deltaY, deltaZ, entityBlockLight, leashHolderBlockLight, entitySkyLight, leashHolderSkyLight, 0.025F, 0.025F, xOffset, zOffset, segments, false);
        }

        for(int segments = 24; segments >= 0; --segments) {
            addVertexPair(vertex, matrix, deltaX, deltaY, deltaZ, entityBlockLight, leashHolderBlockLight, entitySkyLight, leashHolderSkyLight, 0.025F, 0.0F, xOffset, zOffset, segments, true);
        }

        stack.popPose();
    }
    
    
    private static void addVertexPair(
        VertexConsumer vertex,
        Matrix4f matrix,
        float deltaX,
        float deltaY,
        float deltaZ,
        int entityBlockLight,
        int leashHolderBlockLight,
        int entitySkyLight,
        int leashHolderSkyLight,
        float leashThickness,
        float leashOffset,
        float xOffset,
        float zOffset,
        int index,
        boolean oddSegment
    ) {
        float segmentRatio = (float) index / 24.0F;
        int blockLight = (int) Mth.lerp(segmentRatio, (float) entityBlockLight, (float) leashHolderBlockLight);
        int skyLight = (int) Mth.lerp(segmentRatio, (float) entitySkyLight, (float) leashHolderSkyLight);
        int lightMapUV = LightTexture.pack(blockLight, skyLight);
        float brightnessFactor = index % 2 == (oddSegment ? 1 : 0) ? 0.7F : 1.0F;
        float red = 0.5F * brightnessFactor;
        float green = 0.4F * brightnessFactor;
        float blue = 0.3F * brightnessFactor;
        float factorX = deltaX * segmentRatio;
        float factorY = deltaY > 0.0F ? deltaY * segmentRatio * segmentRatio : deltaY - deltaY * (1.0F - segmentRatio) * (1.0F - segmentRatio);
        float factorZ = deltaZ * segmentRatio;
        vertex.vertex(matrix, factorX - xOffset, factorY + leashOffset, factorZ + zOffset).color(red, green, blue, 1.0F).uv2(lightMapUV).endVertex();
        vertex.vertex(matrix, factorX + xOffset, factorY + leashThickness - leashOffset, factorZ - zOffset).color(red, green, blue, 1.0F).uv2(lightMapUV).endVertex();
    }
    
    
    private <E extends Entity> void renderLeashLegacy(T entityLiving, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, E leashHolder) {
        matrixStack.pushPose();
        Vec3 vec3 = leashHolder.getRopeHoldPosition(partialTicks);
        double d = (double)(Mth.lerp(partialTicks, entityLiving.yBodyRot, entityLiving.yBodyRotO) * 0.017453292F) + 1.5707963267948966;
        Vec3 vec32 = entityLiving.getLeashOffset();
        double e = Math.cos(d) * vec32.z + Math.sin(d) * vec32.x;
        double f = Math.sin(d) * vec32.z - Math.cos(d) * vec32.x;
        double g = Mth.lerp(partialTicks, entityLiving.xo, entityLiving.getX()) + e;
        double h = Mth.lerp(partialTicks, entityLiving.yo, entityLiving.getY()) + vec32.y;
        double i = Mth.lerp(partialTicks, entityLiving.zo, entityLiving.getZ()) + f;
        matrixStack.translate(e, vec32.y, f);
        float j = (float)(vec3.x - g);
        float k = (float)(vec3.y - h);
        float l = (float)(vec3.z - i);
        float m = 0.025F;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.leash());
        Matrix4f matrix4f = matrixStack.last().pose();
        float n = Mth.fastInvSqrt(j * j + l * l) * 0.025F / 2.0F;
        float o = l * n;
        float p = j * n;
        BlockPos blockPos = new BlockPos(entityLiving.getEyePosition(partialTicks));
        BlockPos blockPos2 = new BlockPos(leashHolder.getEyePosition(partialTicks));
        int q = this.getBlockLightLevel(entityLiving, blockPos);
        int r = ((EntityRendererAccessor) this.entityRenderDispatcher.getRenderer(leashHolder)).callGetBlockLightLevel(leashHolder, blockPos2);
        int s = entityLiving.level.getBrightness(LightLayer.SKY, blockPos);
        int t = entityLiving.level.getBrightness(LightLayer.SKY, blockPos2);
        renderSide(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, 0.025F, o, p);
        renderSide(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, 0.0F, o, p);
        matrixStack.popPose();
    }
    
    public static void renderSide(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, float h, int i, int j, int k, int l, float m, float n, float o, float p) {
        for(int r = 0; r < 24; ++r) {
            float s = (float)r / 23.0F;
            int t = (int)Mth.lerp(s, (float)i, (float)j);
            int u = (int)Mth.lerp(s, (float)k, (float)l);
            int v = LightTexture.pack(t, u);
            addVertexPair(vertexConsumer, matrix4f, v, f, g, h, m, n, 24, r, false, o, p);
            addVertexPair(vertexConsumer, matrix4f, v, f, g, h, m, n, 24, r + 1, true, o, p);
        }
    }
    
    public static void addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix4f, int i, float f, float g, float h, float j, float k, int l, int m, boolean bl, float n, float o) {
        float p = 0.5F;
        float q = 0.4F;
        float r = 0.3F;
        if (m % 2 == 0) {
            p *= 0.7F;
            q *= 0.7F;
            r *= 0.7F;
        }
        
        float s = (float)m / (float)l;
        float t = f * s;
        float u = g > 0.0F ? g * s * s : g - g * (1.0F - s) * (1.0F - s);
        float v = h * s;
        if (!bl) {
            vertexConsumer.vertex(matrix4f, t + n, u + j - k, v - o).color(p, q, r, 1.0F).uv2(i).endVertex();
        }
        
        vertexConsumer.vertex(matrix4f, t - n, u + k, v + o).color(p, q, r, 1.0F).uv2(i).endVertex();
        if (bl) {
            vertexConsumer.vertex(matrix4f, t + n, u + j - k, v - o).color(p, q, r, 1.0F).uv2(i).endVertex();
        }
    }
}