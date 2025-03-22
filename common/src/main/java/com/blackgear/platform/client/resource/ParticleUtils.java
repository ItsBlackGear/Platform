package com.blackgear.platform.client.resource;

import com.blackgear.platform.common.providers.math.IntProvider;
import com.blackgear.platform.common.providers.math.UniformInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.function.Supplier;

public class ParticleUtils {
    /**
     * Spawns particles on all faces of a block.
     *
     * @param level                 The level in which to spawn the particles.
     * @param pos                   The position of the block.
     * @param particle              The particle to be displayed.
     * @param particleCountProvider The provider for the number of particles to spawn.
     */
    public static void spawnParticleOnBlockFaces(Level level, BlockPos pos, ParticleOptions particle, IntProvider particleCountProvider) {
        for (Direction direction : Direction.values()) {
            spawnParticleOnBlockFace(level, pos, particle, particleCountProvider, direction, () -> {
                return getRandomSpeedRanges(level.random);
            }, 0.55);
        }
    }
    
    /**
     * Spawns particles on a specific face of a block.
     *
     * @param level                 The level in which to spawn the particles.
     * @param pos                   The position of the block.
     * @param particle              The particle to be displayed.
     * @param particleCountProvider The provider for the number of particles to spawn.
     * @param faceDirection         The direction of the block face.
     * @param speedSupplier         The supplied for the particle speed vector.
     * @param speedMultiplier       The multiplier for the particle speed.
     */
    private static void spawnParticleOnBlockFace(Level level, BlockPos pos, ParticleOptions particle, IntProvider particleCountProvider, Direction faceDirection, Supplier<Vec3> speedSupplier, double speedMultiplier) {
        int particleCount = particleCountProvider.sample(level.random);
        
        for (int i = 0; i < particleCount; i++) {
            spawnParticleOnFace(level, pos, faceDirection, particle, speedSupplier.get(), speedMultiplier);
        }
    }
    
    /**
     * Generates a random speed vector within specified ranges.
     *
     * @return The randomly generated speed vector.
     */
    private static Vec3 getRandomSpeedRanges(Random random) {
        return new Vec3(
            Mth.nextDouble(random, -0.5, 0.5),
            Mth.nextDouble(random, -0.5, 0.5),
            Mth.nextDouble(random, -0.5, 0.5)
        );
    }
    
    /**
     * Spawns particles along a specific axis of a block.
     *
     * @param axis          The axis which to spawn the particles.
     * @param level         The level in which to spawn the particles.
     * @param pos           The position of the block.
     * @param range         The range of the particles from the block.
     * @param particle      The particle to be displayed.
     * @param particleCount The number of particles to spawn.
     */
    public static void spawnParticlesAlongAxis(Direction.Axis axis, Level level, BlockPos pos, double range, ParticleOptions particle, UniformInt particleCount) {
        Vec3 centerPos = Vec3.atCenterOf(pos);
        boolean xAxis = axis == Direction.Axis.X;
        boolean yAxis = axis == Direction.Axis.Y;
        boolean zAxis = axis == Direction.Axis.Z;
        int count = particleCount.sample(level.random);
        
        for (int i = 0; i < count; i++) {
            double offsetX = centerPos.x + Mth.nextDouble(level.random, -1.0, 1.0) * (xAxis ? 0.5 : range);
            double offsetY = centerPos.y + Mth.nextDouble(level.random, -1.0, 1.0) * (yAxis ? 0.5 : range);
            double offsetZ = centerPos.z + Mth.nextDouble(level.random, -1.0, 1.0) * (zAxis ? 0.5 : range);
            double speedX = xAxis ? Mth.nextDouble(level.random, -1.0, 1.0) : 0.0;
            double speedY = yAxis ? Mth.nextDouble(level.random, -1.0, 1.0) : 0.0;
            double speedZ = zAxis ? Mth.nextDouble(level.random, -1.0, 1.0) : 0.0;
            level.addParticle(particle, offsetX, offsetY, offsetZ, speedX, speedY, speedZ);
        }
    }
    
    /**
     * Spawns a particle on a specific face of a block.
     *
     * @param level             The level in which to spawn the particle.
     * @param pos               The position of the block.
     * @param faceDirection     The direction of the block face.
     * @param particle          The particle to be displayed.
     * @param speed             The speed vector of the particle.
     * @param speedMultiplier   The multiplier for the particle speed.
     */
    private static void spawnParticleOnFace(Level level, BlockPos pos, Direction faceDirection, ParticleOptions particle, Vec3 speed, double speedMultiplier) {
        Vec3 centerPos = Vec3.atCenterOf(pos);
        int stepX = faceDirection.getStepX();
        int stepY = faceDirection.getStepY();
        int stepZ = faceDirection.getStepZ();
        double offsetX = centerPos.x + (stepX == 0 ? Mth.nextDouble(level.random, -0.5D, 0.5D) : (double) stepX * speedMultiplier);
        double offsetY = centerPos.y + (stepY == 0 ? Mth.nextDouble(level.random, -0.5D, 0.5D) : (double) stepY * speedMultiplier);
        double offsetZ = centerPos.z + (stepZ == 0 ? Mth.nextDouble(level.random, -0.5D, 0.5D) : (double) stepZ * speedMultiplier);
        double speedX = stepX == 0 ? speed.x() : 0.0;
        double speedY = stepY == 0 ? speed.y() : 0.0;
        double speedZ = stepZ == 0 ? speed.z() : 0.0;
        level.addParticle(particle, offsetX, offsetY, offsetZ, speedX, speedY, speedZ);
    }
    
    /**
     * Spawn a particle below a specific block.
     *
     * @param level     The level in which to spawn the particle.
     * @param pos       The position of the block.
     * @param random    The random value.
     * @param particle  The particle to be displayed.
     */
    public static void spawnParticleBelow(Level level, BlockPos pos, Random random, ParticleOptions particle) {
        double posX = (double) pos.getX() + random.nextDouble();
        double posY = (double) pos.getY() - 0.05;
        double posZ = (double) pos.getZ() + random.nextDouble();
        level.addParticle(particle, posX, posY, posZ, 0.0, 0.0, 0.0);
    }
}