package com.mafuyu404.diligentstalker.render;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DroneStalkerModel extends EntityModel<DroneStalkerEntity> {
    public final ModelPart top;
    public final ModelPart bottom;
    public final ModelPart north;
    public final ModelPart south;
    public final ModelPart east;
    public final ModelPart west;

    public DroneStalkerModel(ModelPart root) {
        this.top = root.getChild("top");
        this.bottom = root.getChild("bottom");
        this.north = root.getChild("north");
        this.south = root.getChild("south");
        this.east = root.getChild("east");
        this.west = root.getChild("west");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 顶面（Y=8）
        root.addOrReplaceChild("top",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0, 8, 0, 16, 0, 16),
                PartPose.ZERO);

        // 底面（Y=0）
        root.addOrReplaceChild("bottom",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0, 0, 0, 16, 0, 16),
                PartPose.ZERO);

        // 北面（Z=0）
        root.addOrReplaceChild("north",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0, 0, 0, 16, 12, 0),
                PartPose.ZERO);

        // 南面（Z=16）
        root.addOrReplaceChild("south",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0, 0, 16, 16, 8, 0),
                PartPose.ZERO);

        // 西面（X=0）
        root.addOrReplaceChild("west",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0, 0, 0, 0, 8, 16),
                PartPose.ZERO);

        // 东面（X=16）
        root.addOrReplaceChild("east",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(16, 0, 0, 0, 8, 16),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(DroneStalkerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // 各部分的渲染将在渲染器中单独处理
    }
}
