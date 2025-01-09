package com.blackgear.platform.client.renderer.model.geom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;

import java.util.*;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class NeoModelPart {
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public float zScale = 1.0F;
    public boolean visible = true;
    public boolean skipDraw;
    private final List<Cube> cubes;
    private final Map<String, NeoModelPart> children;
    private PartPose initialPose = PartPose.ZERO;
    
    public NeoModelPart(List<Cube> cubes, Map<String, NeoModelPart> children) {
        this.cubes = cubes;
        this.children = children;
    }
    
    public PartPose storePose() {
        return PartPose.offsetAndRotation(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot);
    }
    
    public PartPose getInitialPose() {
        return this.initialPose;
    }
    
    public void setInitialPose(PartPose part) {
        this.initialPose = part;
    }
    
    public void resetPose() {
        this.loadPose(this.initialPose);
    }
    
    public void loadPose(PartPose part) {
        this.x = part.x;
        this.y = part.y;
        this.z = part.z;
        this.xRot = part.xRot;
        this.yRot = part.yRot;
        this.zRot = part.zRot;
        this.xScale = 1.0F;
        this.yScale = 1.0F;
        this.zScale = 1.0F;
    }
    
    public void copyFrom(NeoModelPart part) {
        this.xScale = part.xScale;
        this.yScale = part.yScale;
        this.zScale = part.zScale;
        this.xRot = part.xRot;
        this.yRot = part.yRot;
        this.zRot = part.zRot;
        this.x = part.x;
        this.y = part.y;
        this.z = part.z;
    }
    
    public boolean hasChild(String partName) {
        return this.children.containsKey(partName);
    }
    
    public NeoModelPart getChild(String partName) {
        NeoModelPart part = this.children.get(partName);
        
        if (part == null) {
            throw new NoSuchElementException("Can't find part " + partName);
        } else {
            return part;
        }
    }
    
    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void setRotation(float xRot, float yRot, float zRot) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
    }
    
    public void render(PoseStack stack, VertexConsumer vertices, int packedLight, int packedOverlay) {
        this.render(stack, vertices, packedLight, packedOverlay, -1);
    }
    
    public void render(PoseStack stack, VertexConsumer vertices, int packedLight, int packedOverlay, int color) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                stack.pushPose();
                this.translateAndRotate(stack);
                if (!this.skipDraw) {
                    this.compile(stack.last(), vertices, packedLight, packedOverlay, color);
                }
                
                for(NeoModelPart part : this.children.values()) {
                    part.render(stack, vertices, packedLight, packedOverlay, color);
                }
                
                stack.popPose();
            }
        }
    }
    
    public void visit(PoseStack stack, Visitor visitor) {
        this.visit(stack, visitor, "");
    }
    
    private void visit(PoseStack stack, Visitor visitor, String partName) {
        if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
            stack.pushPose();
            this.translateAndRotate(stack);
            PoseStack.Pose pose = stack.last();
            
            for(int i = 0; i < this.cubes.size(); ++i) {
                visitor.visit(pose, partName, i, this.cubes.get(i));
            }
            
            String location = partName + "/";
            this.children.forEach((children, part) -> part.visit(stack, visitor, location + children));
            stack.popPose();
        }
    }
    
    public void translateAndRotate(PoseStack stack) {
        stack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
        
        if (this.zRot != 0.0F) {
            stack.mulPose(Vector3f.ZP.rotation(this.zRot));
        }
        
        if (this.yRot != 0.0F) {
            stack.mulPose(Vector3f.YP.rotation(this.yRot));
        }
        
        if (this.xRot != 0.0F) {
            stack.mulPose(Vector3f.XP.rotation(this.xRot));
        }
        
        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            stack.scale(this.xScale, this.yScale, this.zScale);
        }
    }
    
    private void compile(PoseStack.Pose pose, VertexConsumer vertices, int packedLight, int packedOverlay, int color) {
        for(Cube cube : this.cubes) {
            cube.compile(pose, vertices, packedLight, packedOverlay, color);
        }
    }
    
    public Cube getRandomCube(Random random) {
        return this.cubes.get(random.nextInt(this.cubes.size()));
    }
    
    public boolean isEmpty() {
        return this.cubes.isEmpty();
    }
    
    public void offsetPos(Vector3f vector) {
        this.x += vector.x();
        this.y += vector.y();
        this.z += vector.z();
    }
    
    public void offsetRotation(Vector3f vector) {
        this.xRot += vector.x();
        this.yRot += vector.y();
        this.zRot += vector.z();
    }
    
    public void offsetScale(Vector3f vector) {
        this.xScale += vector.x();
        this.yScale += vector.y();
        this.zScale += vector.z();
    }
    
    public Stream<NeoModelPart> getAllParts() {
        return Stream.concat(Stream.of(this), this.children.values().stream().flatMap(NeoModelPart::getAllParts));
    }
    
    @Environment(EnvType.CLIENT)
    public static class Cube {
        private final Polygon[] polygons;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;
        
        public Cube(
            int texCoordU,
            int texCoordV,
            float originX,
            float originY,
            float originZ,
            float dimensionsX,
            float dimensionsY,
            float dimensionsZ,
            float growX,
            float growY,
            float growZ,
            boolean mirror,
            float texScaleU,
            float texScaleV,
            Set<Direction> visibleFaces
        ) {
            this.minX = originX;
            this.minY = originY;
            this.minZ = originZ;
            this.maxX = originX + dimensionsX;
            this.maxY = originY + dimensionsY;
            this.maxZ = originZ + dimensionsZ;
            this.polygons = new Polygon[visibleFaces.size()];
            float maxX = originX + dimensionsX;
            float maxY = originY + dimensionsY;
            float maxZ = originZ + dimensionsZ;
            originX -= growX;
            originY -= growY;
            originZ -= growZ;
            maxX += growX;
            maxY += growY;
            maxZ += growZ;
            
            if (mirror) {
                float offset = maxX;
                maxX = originX;
                originX = offset;
            }
            
            Vertex vertex = new Vertex(originX, originY, originZ, 0.0F, 0.0F);
            Vertex vertex2 = new Vertex(maxX, originY, originZ, 0.0F, 8.0F);
            Vertex vertex3 = new Vertex(maxX, maxY, originZ, 8.0F, 8.0F);
            Vertex vertex4 = new Vertex(originX, maxY, originZ, 8.0F, 0.0F);
            Vertex vertex5 = new Vertex(originX, originY, maxZ, 0.0F, 0.0F);
            Vertex vertex6 = new Vertex(maxX, originY, maxZ, 0.0F, 8.0F);
            Vertex vertex7 = new Vertex(maxX, maxY, maxZ, 8.0F, 8.0F);
            Vertex vertex8 = new Vertex(originX, maxY, maxZ, 8.0F, 0.0F);
            
            float texU1 = (float) texCoordU;
            float texU2 = (float) texCoordU + dimensionsZ;
            float texU3 = (float) texCoordU + dimensionsZ + dimensionsX;
            float texU4 = (float) texCoordU + dimensionsZ + dimensionsX + dimensionsX;
            float texU5 = (float) texCoordU + dimensionsZ + dimensionsX + dimensionsZ;
            float texU6 = (float) texCoordU + dimensionsZ + dimensionsX + dimensionsZ + dimensionsX;
            float texV1 = (float) texCoordV;
            float texV2 = (float) texCoordV + dimensionsZ;
            float texV3 = (float) texCoordV + dimensionsZ + dimensionsY;
            int index = 0;
            
            if (visibleFaces.contains(Direction.DOWN)) {
                this.polygons[index++] = new Polygon(new Vertex[]{vertex6, vertex5, vertex, vertex2}, texU2, texV1, texU3, texV2, texScaleU, texScaleV, mirror, Direction.DOWN);
            }
            
            if (visibleFaces.contains(Direction.UP)) {
                this.polygons[index++] = new Polygon(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, texU3, texV2, texU4, texV1, texScaleU, texScaleV, mirror, Direction.UP);
            }
            
            if (visibleFaces.contains(Direction.WEST)) {
                this.polygons[index++] = new Polygon(new Vertex[]{vertex, vertex5, vertex8, vertex4}, texU1, texV2, texU2, texV3, texScaleU, texScaleV, mirror, Direction.WEST);
            }
            
            if (visibleFaces.contains(Direction.NORTH)) {
                this.polygons[index++] = new Polygon(new Vertex[]{vertex2, vertex, vertex4, vertex3}, texU2, texV2, texU3, texV3, texScaleU, texScaleV, mirror, Direction.NORTH);
            }
            
            if (visibleFaces.contains(Direction.EAST)) {
                this.polygons[index++] = new Polygon(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, texU3, texV2, texU5, texV3, texScaleU, texScaleV, mirror, Direction.EAST);
            }
            
            if (visibleFaces.contains(Direction.SOUTH)) {
                this.polygons[index] = new Polygon(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, texU5, texV2, texU6, texV3, texScaleU, texScaleV, mirror, Direction.SOUTH);
            }
        }
        
        //TODO: possible failure...
        public void compile(PoseStack.Pose pose, VertexConsumer vertices, int packedLight, int packedOverlay, int color) {
            Matrix4f matrix = pose.pose();
            
            for(Polygon polygon : this.polygons) {
                Vector3f normal = polygon.normal.copy();
                normal.transform(pose.normal());
                float normalX = normal.x();
                float normalY = normal.y();
                float normalZ = normal.z();

                for(Vertex vertex : polygon.vertices) {
                    float x = vertex.pos.x() / 16.0F;
                    float y = vertex.pos.y() / 16.0F;
                    float z = vertex.pos.z() / 16.0F;
                    Vector4f vector = new Vector4f(x, y, z, 1.0F);
                    vector.transform(matrix);
                    vertices.vertex(
                        vector.x(),
                        vector.y(),
                        vector.z(),
                        FastColor.ARGB32.red(color),
                        FastColor.ARGB32.green(color),
                        FastColor.ARGB32.blue(color),
                        FastColor.ARGB32.alpha(color),
                        vertex.u,
                        vertex.v,
                        packedOverlay,
                        packedLight,
                        normalX,
                        normalY,
                        normalZ
                    );
                }
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Polygon {
        public final Vertex[] vertices;
        public final Vector3f normal;
        
        public Polygon(
            Vertex[] vertices,
            float u1,
            float v1,
            float u2,
            float v2,
            float texWidth,
            float texHeight,
            boolean mirror,
            Direction direction
        ) {
            this.vertices = vertices;
            float uOffset = 0.0F / texWidth;
            float vOffset = 0.0F / texHeight;
            vertices[0] = vertices[0].remap(u2 / texWidth - uOffset, v1 / texHeight + vOffset);
            vertices[1] = vertices[1].remap(u1 / texWidth + uOffset, v1 / texHeight + vOffset);
            vertices[2] = vertices[2].remap(u1 / texWidth + uOffset, v2 / texHeight - vOffset);
            vertices[3] = vertices[3].remap(u2 / texWidth - uOffset, v2 / texHeight - vOffset);
            
            if (mirror) {
                int vertexCount = vertices.length;
                
                for(int i = 0; i < vertexCount / 2; i++) {
                    Vertex vertex = vertices[i];
                    vertices[i] = vertices[vertexCount - 1 - i];
                    vertices[vertexCount - 1 - i] = vertex;
                }
            }
            
            this.normal = direction.step();
            
            if (mirror) {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;
        
        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }
        
        public Vertex remap(float u, float v) {
            return new Vertex(this.pos, u, v);
        }
        
        public Vertex(Vector3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }
    
    @Environment(EnvType.CLIENT) @FunctionalInterface
    public interface Visitor {
        void visit(PoseStack.Pose pose, String string, int i, Cube cube);
    }
}