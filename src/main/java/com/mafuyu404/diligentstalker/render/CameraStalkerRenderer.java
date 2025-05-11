package com.mafuyu404.diligentstalker.render;

import com.mafuyu404.diligentstalker.entity.CameraStalkerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class CameraStalkerRenderer implements BlockEntityRenderer<CameraStalkerBlockEntity> {
    private final BlockRenderDispatcher renderer;

    public CameraStalkerRenderer(BlockEntityRendererProvider.Context context) {
        renderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(CameraStalkerBlockEntity entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        BlockState blockState = entity.getBlockState();
        BakedModel model = renderer.getBlockModel(blockState);

        poseStack.pushPose();

        // 获取纹理
        TextureAtlasSprite sideSprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(new ResourceLocation("diligentstalker:block/camera_stalker_side"));

        TextureAtlasSprite topSprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(new ResourceLocation("diligentstalker:block/camera_stalker_top"));

        // 在Fabric中使用VertexConsumer和RenderContext
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());

        RenderContext context = createRenderContext(buffer, poseStack, packedLight, packedOverlay);
        model.emitBlockQuads(entity.getLevel(), blockState, entity.getBlockPos(),
                () -> null, context);

        poseStack.popPose();
    }

    // 创建一个简单的RenderContext实现
    private RenderContext createRenderContext(VertexConsumer buffer, PoseStack poseStack,
                                              int packedLight, int packedOverlay) {
        return new RenderContext() {
            private final QuadEmitter emitter = new QuadEmitter() {
                @Override
                public float x (int vertexIndex) {
                    return 0;
                }

                @Override
                public float y (int vertexIndex) {
                    return 0;
                }

                @Override
                public float z (int vertexIndex) {
                    return 0;
                }

                @Override
                public float posByIndex (int vertexIndex, int coordinateIndex) {
                    return 0;
                }

                @Override
                public Vector3f copyPos (int vertexIndex, @Nullable Vector3f target) {
                    return null;
                }

                @Override
                public int color (int vertexIndex) {
                    return 0;
                }

                @Override
                public float u (int vertexIndex) {
                    return 0;
                }

                @Override
                public float v (int vertexIndex) {
                    return 0;
                }

                @Override
                public Vector2f copyUv (int vertexIndex, @Nullable Vector2f target) {
                    return null;
                }

                @Override
                public int lightmap (int vertexIndex) {
                    return 0;
                }

                @Override
                public boolean hasNormal (int vertexIndex) {
                    return false;
                }

                @Override
                public float normalX (int vertexIndex) {
                    return 0;
                }

                @Override
                public float normalY (int vertexIndex) {
                    return 0;
                }

                @Override
                public float normalZ (int vertexIndex) {
                    return 0;
                }

                @Override
                public @Nullable Vector3f copyNormal (int vertexIndex, @Nullable Vector3f target) {
                    return null;
                }

                @Override
                public @Nullable Direction cullFace () {
                    return null;
                }

                @Override
                public @NotNull Direction lightFace () {
                    return null;
                }

                @Override
                public @Nullable Direction nominalFace () {
                    return null;
                }

                @Override
                public Vector3f faceNormal () {
                    return null;
                }

                @Override
                public RenderMaterial material () {
                    return null;
                }

                @Override
                public int colorIndex () {
                    return 0;
                }

                @Override
                public int tag () {
                    return 0;
                }

                @Override
                public void toVanilla (int[] target, int targetIndex) {

                }

                @Override
                public QuadEmitter pos (int vertexIndex, float x, float y, float z) {
                    return null;
                }

                @Override
                public QuadEmitter color (int vertexIndex, int color) {
                    return null;
                }

                @Override
                public QuadEmitter uv (int vertexIndex, float u, float v) {
                    return null;
                }

                @Override
                public QuadEmitter spriteBake (TextureAtlasSprite sprite, int bakeFlags) {
                    return null;
                }

                @Override
                public QuadEmitter lightmap (int vertexIndex, int lightmap) {
                    return null;
                }

                @Override
                public QuadEmitter normal (int vertexIndex, float x, float y, float z) {
                    return null;
                }

                @Override
                public QuadEmitter cullFace (@Nullable Direction face) {
                    return null;
                }

                @Override
                public QuadEmitter nominalFace (@Nullable Direction face) {
                    return null;
                }

                @Override
                public QuadEmitter material (RenderMaterial material) {
                    return null;
                }

                @Override
                public QuadEmitter colorIndex (int colorIndex) {
                    return null;
                }

                @Override
                public QuadEmitter tag (int tag) {
                    return null;
                }

                @Override
                public QuadEmitter copyFrom (QuadView quad) {
                    return null;
                }

                @Override
                public QuadEmitter fromVanilla (int[] quadData, int startIndex) {
                    return null;
                }

                @Override
                public QuadEmitter fromVanilla (BakedQuad quad, RenderMaterial material, @Nullable Direction cullFace) {
                    return null;
                }

                @Override
                public QuadEmitter emit() {
                    // 实现四边形发射逻辑
                    return this;
                }
            };
            
            private final java.util.List<QuadTransform> transforms = new java.util.ArrayList<>();
            
            @Override
            public QuadEmitter getEmitter() {
                return emitter;
            }
            
            @Override
            public boolean hasTransform() {
                return !transforms.isEmpty();
            }
            
            @Override
            public void pushTransform(QuadTransform transform) {
                transforms.add(transform);
            }
            
            @Override
            public void popTransform() {
                if (!transforms.isEmpty()) {
                    transforms.remove(transforms.size() - 1);
                }
            }
            
            @Override
            public boolean isFaceCulled(@Nullable Direction face) {
                return false; // 简化实现，不进行面剔除
            }
            
            @Override
            public ItemDisplayContext itemTransformationMode() {
                return ItemDisplayContext.NONE;
            }

            @Override
            public BakedModelConsumer bakedModelConsumer() {
                return new BakedModelConsumer() {
                    @Override
                    public void accept(BakedModel model) {
                        // 处理模型渲染
                        // 在这里可以使用buffer和poseStack来渲染模型
                    }

                    @Override
                    public void accept(BakedModel model, @Nullable BlockState state) {
                        // 处理带有特定BlockState的模型渲染
                    }
                };
            }
        };
    }
}