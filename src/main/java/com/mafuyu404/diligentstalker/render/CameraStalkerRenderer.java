package com.mafuyu404.diligentstalker.render;

import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mafuyu404.diligentstalker.render.model.data.ModelData;
import com.mafuyu404.diligentstalker.render.model.data.ModelProperty;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CameraStalkerRenderer implements BlockEntityRenderer<CameraStalkerBlockEntity> {
    private static final ModelProperty<TextureAtlasSprite> SIDE_TEXTURE = new ModelProperty<>();
    private static final ModelProperty<TextureAtlasSprite> TOP_TEXTURE = new ModelProperty<>();
    private final BlockRenderDispatcher renderer;

    public CameraStalkerRenderer(BlockEntityRendererProvider.Context context) {
        renderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(CameraStalkerBlockEntity entity,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay) {
        BlockState state = entity.getBlockState();
        BakedModel model = renderer.getBlockModel(state);

        TextureAtlasSprite sideSprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(new ResourceLocation("diligentstalker:block/camera_stalker_side"));
        TextureAtlasSprite topSprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(new ResourceLocation("diligentstalker:block/camera_stalker_top"));

        ModelData data = ModelData.builder()
                .with(SIDE_TEXTURE, sideSprite)
                .with(TOP_TEXTURE, topSprite)
                .build();

        RenderType type = RenderType.solid();
        VertexConsumer consumer = bufferSource.getBuffer(type);

        RandomSource random = RandomSource.create();

        // 按方向迭代并渲染
        for (Direction dir : Direction.values()) {
            random.setSeed(42L);
            List<BakedQuad> quads = model.getQuads(state, dir, random);
            for (BakedQuad quad : quads) {
                consumer.putBulkData(poseStack.last(), quad, 1.0f, 1.0f, 1.0f, packedLight, packedOverlay);
            }
        }

        // 渲染那部分无方向的面
        random.setSeed(42L);
        List<BakedQuad> quads = model.getQuads(state, null, random);
        for (BakedQuad quad : quads) {
            consumer.putBulkData(poseStack.last(), quad, 1.0f, 1.0f, 1.0f, packedLight, packedOverlay);
        }
    }
}