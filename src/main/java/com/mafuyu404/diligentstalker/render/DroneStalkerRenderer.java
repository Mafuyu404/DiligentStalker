package com.mafuyu404.diligentstalker.render;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class DroneStalkerRenderer extends EntityRenderer<DroneStalkerEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(DiligentStalker.MODID, "drone_stalker"), "main");
    private static final ResourceLocation TOP_TEXTURE = new ResourceLocation(DiligentStalker.MODID, "textures/entity/drone_stalker_top.png");
    private static final ResourceLocation BOTTOM_TEXTURE = new ResourceLocation(DiligentStalker.MODID, "textures/entity/drone_stalker_bottom.png");
    private static final ResourceLocation FORWARD_TEXTURE = new ResourceLocation(DiligentStalker.MODID, "textures/entity/drone_stalker_forward.png");
    private static final ResourceLocation SIDE_TEXTURE = new ResourceLocation(DiligentStalker.MODID, "textures/entity/drone_stalker_side.png");
    private final DroneStalkerModel model;

    public DroneStalkerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new DroneStalkerModel(context.bakeLayer(LAYER));
    }

    @Override
    public void render(DroneStalkerEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.yRotO));
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.translate(-0.5, 0, -0.5);

        // 渲染顶面
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TOP_TEXTURE));
        this.model.top.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        // 渲染底面
        consumer = buffer.getBuffer(RenderType.entityCutout(BOTTOM_TEXTURE));
        this.model.bottom.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        // 渲染北面（前）
        consumer = buffer.getBuffer(RenderType.entityCutout(FORWARD_TEXTURE));
        this.model.north.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        // 渲染南面（后）
        consumer = buffer.getBuffer(RenderType.entityCutout(SIDE_TEXTURE));
        this.model.south.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        // 渲染东面和西面（侧边）
        consumer = buffer.getBuffer(RenderType.entityCutout(SIDE_TEXTURE));
        this.model.east.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        this.model.west.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DroneStalkerEntity entity) {
        return TOP_TEXTURE; // 此处返回值不影响实际渲染，因为各面已单独处理
    }
}
