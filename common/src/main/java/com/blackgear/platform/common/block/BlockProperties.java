package com.blackgear.platform.common.block;

import com.blackgear.platform.core.util.function.ToFloatFunction;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class BlockProperties {
    public static Builder of(Material material) {
        return new Builder(BlockBehaviour.Properties.of(material));
    }
    
    public static Builder of(Material material, DyeColor color) {
        return new Builder(BlockBehaviour.Properties.of(material, color));
    }
    
    public static Builder of(Material material, MaterialColor color) {
        return new Builder(BlockBehaviour.Properties.of(material, color));
    }
    
    public static Builder of(Material material, Function<BlockState, MaterialColor> colorByState) {
        return new Builder(BlockBehaviour.Properties.of(material, colorByState));
    }
    
    public static Builder copy(Block block) {
        return new Builder(BlockBehaviour.Properties.copy(block));
    }
    
    public static class Builder {
        private final BlockBehaviour.Properties properties;
        
        public Builder(BlockBehaviour.Properties properties) {
            this.properties = properties;
        }
        
        public Builder color(MaterialColor color) {
            this.properties.materialColor = state -> color;
            return this;
        }
        
        public Builder noCollision() {
            this.properties.noCollission();
            return this;
        }
        
        public Builder noOcclusion() {
            this.properties.noOcclusion();
            return this;
        }
        
        public Builder friction(float friction) {
            this.properties.friction(friction);
            return this;
        }
        
        public Builder speedFactor(float speedFactor) {
            this.properties.speedFactor(speedFactor);
            return this;
        }
        
        public Builder jumpFactor(float jumpFactor) {
            this.properties.jumpFactor(jumpFactor);
            return this;
        }
        
        public Builder sound(SoundType sound) {
            this.properties.sound(sound);
            return this;
        }
        
        public Builder lightLevel(ToIntFunction<BlockState> lightByState) {
            this.properties.lightLevel(lightByState);
            return this;
        }
        
        public Builder strength(float destroyTime, float explosionResistance) {
            this.properties.strength(destroyTime, explosionResistance);
            return this;
        }
        
        public Builder instabreak() {
            this.properties.instabreak();
            return this;
        }
        
        public Builder strength(float strength) {
            this.properties.strength(strength, strength);
            return this;
        }
        
        public Builder randomTicks() {
            this.properties.randomTicks();
            return this;
        }
        
        public Builder dynamicShape() {
            this.properties.dynamicShape();
            return this;
        }
        
        public Builder noDrops() {
            this.properties.noDrops();
            return this;
        }
        
        public Builder dropsLike(Block block) {
            this.properties.dropsLike(block);
            return this;
        }
        
        public Builder air() {
            this.properties.air();
            return this;
        }
        
        public Builder isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> isValidSpawn) {
            this.properties.isValidSpawn(isValidSpawn);
            return this;
        }
        
        public Builder isRedstoneConductor(BlockBehaviour.StatePredicate isRedstoneConductor) {
            this.properties.isRedstoneConductor(isRedstoneConductor);
            return this;
        }
        
        public Builder isSuffocating(BlockBehaviour.StatePredicate isSuffocating) {
            this.properties.isSuffocating(isSuffocating);
            return this;
        }
        
        public Builder isViewBlocking(BlockBehaviour.StatePredicate isViewBlocking) {
            this.properties.isViewBlocking(isViewBlocking);
            return this;
        }
        
        public Builder hasPostProcess(BlockBehaviour.StatePredicate hasPostProcess) {
            this.properties.hasPostProcess(hasPostProcess);
            return this;
        }
        
        public Builder emissiveRendering(BlockBehaviour.StatePredicate emissiveRendering) {
            this.properties.emissiveRendering(emissiveRendering);
            return this;
        }
        
        public Builder requiresCorrectToolForDrops() {
            this.properties.requiresCorrectToolForDrops();
            return this;
        }
        
        public Builder correctToolForDrops(ToolType toolType, int level) {
            registerToolType(this.properties, toolType, level);
            return this;
        }
        
        public Builder correctToolForDrops(ToolType toolType) {
            registerToolType(this.properties, toolType, 0);
            return this;
        }
        
        @ExpectPlatform
        public static void registerToolType(BlockBehaviour.Properties properties, ToolType toolType, int level) {
            throw new AssertionError();
        }
        
        public Builder pushReaction(PushReaction reaction) {
            ((BlockPropertiesExtension) this.properties).setPushReaction(reaction);
            return this;
        }
        
        public Builder offsetType(BlockBehaviour.OffsetType offset) {
            ((BlockPropertiesExtension) this.properties).setOffsetType(offset);
            return this;
        }
        
        public Builder maxHorizontalOffset(ToFloatFunction<BlockState> offsetByState) {
            ((BlockPropertiesExtension) this.properties).setMaxHorizontalOffset(offsetByState);
            return this;
        }
        
        public Builder maxVerticalOffset(ToFloatFunction<BlockState> offsetByState) {
            ((BlockPropertiesExtension) this.properties).setMaxVerticalOffset(offsetByState);
            return this;
        }
        
        public BlockBehaviour.Properties build() {
            return this.properties;
        }
    }
    
    public static boolean never(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entity) {
        return false;
    }
    
    public static boolean always(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entity) {
        return true;
    }
    
    public static boolean ocelotOrParrot(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }
    
    public static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
    
    public static boolean always(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}