package com.mafuyu404.diligentstalker.render;

import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class CameraStalkerRenderer implements BlockEntityRenderer<CameraStalkerBlockEntity> {
    private static final ModelProperty<TextureAtlasSprite> SIDE_TEXTURE = new ModelProperty<>();
    private static final ModelProperty<TextureAtlasSprite> TOP_TEXTURE = new ModelProperty<>();
    private final BlockRenderDispatcher renderer;

    public CameraStalkerRenderer(BlockEntityRendererProvider.Context context) {
        renderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(CameraStalkerBlockEntity entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        BakedModel model = renderer.getBlockModel(entity.getBlockState());

        poseStack.pushPose();

        TextureAtlasSprite sideSprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(new ResourceLocation("diligentstalker:block/camera_stalker_side"));

        TextureAtlasSprite topSprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(new ResourceLocation("diligentstalker:block/camera_stalker_top"));

        ModelData.Builder modelData = ModelData.builder()
                .with(SIDE_TEXTURE, sideSprite)
                .with(TOP_TEXTURE, topSprite);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.solid()),
                entity.getBlockState(),
                model,
                1f, 1f, 1f,
                packedLight,
                packedOverlay,
                modelData.build(),
                RenderType.solid()
        );

        poseStack.popPose();
    }
}
