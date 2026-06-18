package com.mafuyu404.diligentstalker.render;

import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;

public class CameraStalkerRenderer implements BlockEntityRenderer<CameraStalkerBlockEntity> {
    private final BlockRenderDispatcher renderer;

    public CameraStalkerRenderer(BlockEntityRendererProvider.Context context) {
        renderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(CameraStalkerBlockEntity entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        BakedModel model = renderer.getBlockModel(entity.getBlockState());
        poseStack.pushPose();


        renderer.getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.solid()),
                entity.getBlockState(),
                model,
                1f, 1f, 1f,
                packedLight,
                packedLight
        );

        poseStack.popPose();
    }
}
