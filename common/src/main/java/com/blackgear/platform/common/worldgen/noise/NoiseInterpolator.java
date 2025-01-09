package com.blackgear.platform.common.worldgen.noise;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

public class NoiseInterpolator {
    private double[][] slice0, slice1;
    private final int cellCountY, cellCountZ;
    private final int cellMinY;
    private final NoiseColumnFiller noiseColumnFiller;
    private double noise000, noise001, noise100, noise101, noise010, noise011, noise110, noise111;
    private double valueXZ00, valueXZ10, valueXZ01, valueXZ11;
    private double valueZ0, valueZ1;
    private final int firstCellXInChunk, firstCellZInChunk;
    
    public NoiseInterpolator(
        int cellCountX,
        int cellCountY,
        int cellCountZ,
        ChunkPos chunkPos,
        int cellMinY,
        NoiseColumnFiller filler
    ) {
        this.cellCountY = cellCountY;
        this.cellCountZ = cellCountZ;
        this.cellMinY = cellMinY;
        this.noiseColumnFiller = filler;
        this.slice0 = allocateSlice(cellCountY, cellCountZ);
        this.slice1 = allocateSlice(cellCountY, cellCountZ);
        this.firstCellXInChunk = chunkPos.x * cellCountX;
        this.firstCellZInChunk = chunkPos.z * cellCountZ;
    }
    
    private static double[][] allocateSlice(int cellCountY, int cellCountZ) {
        int noiseZ = cellCountZ + 1;
        int noiseY = cellCountY + 1;
        double[][] buffer = new double[noiseZ][noiseY];
        
        for (int i = 0; i < noiseZ; i++) {
            buffer[i] = new double[noiseY];
        }
        
        return buffer;
    }
    
    public void initializeForFirstCellX() {
        this.fillSlice(this.slice0, this.firstCellXInChunk);
    }
    
    public void advanceCellX(int i) {
        this.fillSlice(this.slice1, this.firstCellXInChunk + i + 1);
    }
    
    private void fillSlice(double[][] slice, int x) {
        for(int cellZ = 0; cellZ < this.cellCountZ + 1; cellZ++) {
            int z = this.firstCellZInChunk + cellZ;
            this.noiseColumnFiller.fillNoiseColumn(slice[cellZ], x, z, this.cellMinY, this.cellCountY);
        }
    }
    
    public void selectCellYZ(int cellCountY, int cellCountZ) {
        this.noise000 = this.slice0[cellCountZ][cellCountY];
        this.noise001 = this.slice0[cellCountZ + 1][cellCountY];
        this.noise100 = this.slice1[cellCountZ][cellCountY];
        this.noise101 = this.slice1[cellCountZ + 1][cellCountY];
        this.noise010 = this.slice0[cellCountZ][cellCountY + 1];
        this.noise011 = this.slice0[cellCountZ + 1][cellCountY + 1];
        this.noise110 = this.slice1[cellCountZ][cellCountY + 1];
        this.noise111 = this.slice1[cellCountZ + 1][cellCountY + 1];
    }
    
    public void updateForY(double factorY) {
        this.valueXZ00 = Mth.lerp(factorY, this.noise000, this.noise010);
        this.valueXZ10 = Mth.lerp(factorY, this.noise100, this.noise110);
        this.valueXZ01 = Mth.lerp(factorY, this.noise001, this.noise011);
        this.valueXZ11 = Mth.lerp(factorY, this.noise101, this.noise111);
    }
    
    public void updateForX(double factorX) {
        this.valueZ0 = Mth.lerp(factorX, this.valueXZ00, this.valueXZ10);
        this.valueZ1 = Mth.lerp(factorX, this.valueXZ01, this.valueXZ11);
    }
    
    public double calculateValue(double factorZ) {
        return Mth.lerp(factorZ, this.valueZ0, this.valueZ1);
    }
    
    public void swapSlices() {
        double[][] slice = this.slice0;
        this.slice0 = this.slice1;
        this.slice1 = slice;
    }
    
    @FunctionalInterface
    public interface NoiseColumnFiller {
        void fillNoiseColumn(double[] slice, int x, int z, int minY, int cellCountY);
    }
}