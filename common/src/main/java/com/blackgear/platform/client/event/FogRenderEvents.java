package com.blackgear.platform.client.event;

import com.blackgear.platform.client.resource.NearPlane;
import com.blackgear.platform.core.mixin.client.access.CameraAccessor;
import com.blackgear.platform.core.util.event.Event;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class FogRenderEvents {
    public static final Event<FogColor> FOG_COLOR = Event.create(FogColor.class);
    public static final Event<FogRendering> FOG_RENDERING = Event.create(FogRendering.class);
    
    protected static BlockState getStateOnCamera(Camera camera) {
        if (camera.isInitialized()) {
            NearPlane plane = NearPlane.getNearPlane(camera);
            CameraAccessor access = (CameraAccessor) camera;
            
            for (Vec3 vector : Arrays.asList(plane.forward, plane.getTopLeft(), plane.getTopRight(), plane.getBottomLeft(), plane.getBottomRight())) {
                Vec3 position = camera.getPosition().add(vector);
                return access.getLevel().getBlockState(new BlockPos(position));
            }
        }
        
        return Blocks.AIR.defaultBlockState();
    }
    
    @FunctionalInterface
    public interface FogColor {
        void setupColor(GameRenderer renderer, ColorContext context, float tickDelta);
    }
    
    public interface ColorContext {
        Camera getCamera();
        
        float getRed();
        
        float getGreen();
        
        float getBlue();
        
        void setRed(float red);
        
        void setGreen(float green);
        
        void setBlue(float blue);
        
        default BlockState getStateAtCamera() {
            return getStateOnCamera(this.getCamera());
        }
        
        boolean isValid();
        
        void build();
    }
    
    @FunctionalInterface
    public interface FogRendering {
        void setupRendering(GameRenderer renderer, RenderContext context, float farPlaneDistance);
    }
    
    public interface RenderContext {
        Camera camera();
        
        float fogStart();
        
        float fogEnd();
        
        float fogDensity();
        
        void fogStart(float start);
        
        void fogEnd(float end);
        
        void fogDensity(float density);
        
        default void fogMode(GlStateManager.FogMode mode) {
            RenderSystem.fogMode(mode);
        }
        
        default void setupNvFogDistance() {
            RenderSystem.setupNvFogDistance();
        }
        
        default BlockState getStateAtCamera() {
            return getStateOnCamera(this.camera());
        }
        
        boolean isValid();
        
        void build();
    }
}