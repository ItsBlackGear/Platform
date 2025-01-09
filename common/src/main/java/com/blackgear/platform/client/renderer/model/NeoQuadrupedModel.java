package com.blackgear.platform.client.renderer.model;

import com.blackgear.platform.client.renderer.model.geom.NeoModelPart;
import com.blackgear.platform.client.renderer.model.geom.PartPose;
import com.blackgear.platform.client.renderer.model.geom.builder.CubeDeformation;
import com.blackgear.platform.client.renderer.model.geom.builder.CubeListBuilder;
import com.blackgear.platform.client.renderer.model.geom.builder.MeshDefinition;
import com.blackgear.platform.client.renderer.model.geom.builder.PartDefinition;
import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class NeoQuadrupedModel<T extends Entity> extends NeoAgeableListModel<T> {
    protected final NeoModelPart head;
    protected final NeoModelPart body;
    protected final NeoModelPart rightHindLeg;
    protected final NeoModelPart leftHindLeg;
    protected final NeoModelPart rightFrontLeg;
    protected final NeoModelPart leftFrontLeg;
    
    protected NeoQuadrupedModel(
        NeoModelPart root,
        boolean scaleHead,
        float babyYHeadOffset,
        float babyZHeadOffset,
        float babyHeadScale,
        float babyBodyScale,
        int bodyYOffset
    ) {
        super(scaleHead, babyYHeadOffset, babyZHeadOffset, babyHeadScale, babyBodyScale, (float) bodyYOffset);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
    }
    
    public static MeshDefinition createBodyMesh(int i, CubeDeformation deformation) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
            "head",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, deformation),
            PartPose.offset(0.0F, (float) (18 - i), -6.0F)
        );
        root.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, deformation),
            PartPose.offsetAndRotation(0.0F, (float) (17 - i), 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float) i, 4.0F, deformation);
        root.addOrReplaceChild("right_hind_leg", leg, PartPose.offset(-3.0F, (float) (24 - i), 7.0F));
        root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(3.0F, (float) (24 - i), 7.0F));
        root.addOrReplaceChild("right_front_leg", leg, PartPose.offset(-3.0F, (float) (24 - i), -5.0F));
        root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(3.0F, (float) (24 - i), -5.0F));
        return mesh;
    }
    
    @Override
    protected Iterable<NeoModelPart> headParts() {
        return ImmutableList.of(this.head);
    }
    
    @Override
    protected Iterable<NeoModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
    }
    
    @Override
    public void setupAnim(T entity, float walkAnimationPos, float walkAnimationSpeed, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * (float) (Math.PI / 180.0);
        this.head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        this.rightHindLeg.xRot = Mth.cos(walkAnimationPos * 0.6662F) * 1.4F * walkAnimationSpeed;
        this.leftHindLeg.xRot = Mth.cos(walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * walkAnimationSpeed;
        this.rightFrontLeg.xRot = Mth.cos(walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * walkAnimationSpeed;
        this.leftFrontLeg.xRot = Mth.cos(walkAnimationPos * 0.6662F) * 1.4F * walkAnimationSpeed;
    }
}
