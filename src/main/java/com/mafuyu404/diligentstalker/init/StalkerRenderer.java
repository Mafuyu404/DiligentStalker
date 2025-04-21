package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.model.DroneStalkerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class StalkerRenderer extends EntityRenderer<DroneStalkerEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(DiligentStalker.MODID, "textures/entity/drone_stalker.png");
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(DiligentStalker.MODID, "drone_stalker"), "main");
    private final DroneStalkerModel model;

    public StalkerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new DroneStalkerModel(context.bakeLayer(LAYER));
    }

    @Override
    public void render(DroneStalkerEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.translate(-0.5, 0, -0.5); // 调整渲染中心点
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DroneStalkerEntity entity) {
        return TEXTURE;
    }
}
