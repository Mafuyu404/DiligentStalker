package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.init.Stalker;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DroneStalkerHUD {
    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            Player player = Minecraft.getInstance().player;
            if (Stalker.hasInstanceOf(player)) {
                drawHud(event.getGuiGraphics(), event.getPartialTick());
            }
        }
    }
    private static void drawHud(GuiGraphics guiGraphics, float partialTicks) {
        PoseStack poseStack = guiGraphics.pose();
        Window window = Minecraft.getInstance().getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        // 圆环基础参数
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        float radius = 80.0f;
        float thickness = 3.0f;

        List<ArcSection> sections = List.of(
                new ArcSection(67.5f+135f, 0.375f, 0.7f, 0.7f, 0.7f, 0.7f),  // 左上灰色
                new ArcSection(67.5f+270f,  0.375f, 0.6f, 0.8f, 1.0f, 0.5f),  // 右上淡蓝
                new ArcSection(112.5f, 0.125f, 1.0f, 0.6f, 0.6f, 0.5f), // 左下淡红
                new ArcSection(67.5f, 0.125f, 0.6f, 1.0f, 0.6f, 0.5f)  // 右下淡绿
        );

        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
//        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionShader);

        Tesselator tessellator = Tesselator.getInstance();

        // 总可用角度（360度 - 3个间隙）
        final float GAP = 2f;
        final float TOTAL_AVAILABLE = 360f - 3 * GAP;

        for (ArcSection section : sections) {
            // 计算实际角度范围
            float sectionAngle = TOTAL_AVAILABLE * section.percentage;

            // 计算起止角度（中心向两侧扩展）
            float start = section.centerAngle - sectionAngle/2 + GAP/2;
            float end = section.centerAngle + sectionAngle/2 - GAP/2;

            // 生成顶点
            BufferBuilder buffer = tessellator.getBuilder();
            buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION);

            RenderSystem.setShaderColor(section.r, section.g, section.b, section.a);

            // 分段数根据角度比例动态计算
            int segments = (int) (sectionAngle * 1.5f);
            for (int i = 0; i <= segments; i++) {
                double angle = Math.toRadians(start + (end - start) * i / segments);

                // 外圈顶点
                double ox = centerX + radius * Math.cos(angle);
                double oy = centerY + radius * Math.sin(angle);

                // 内圈顶点
                double ix = centerX + (radius - thickness) * Math.cos(angle);
                double iy = centerY + (radius - thickness) * Math.sin(angle);

                buffer.vertex(ox, oy, 0).endVertex();
                buffer.vertex(ix, iy, 0).endVertex();
            }

            tessellator.end();
        }

//        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    // 辅助类定义
    private static class ArcSection {
        final float centerAngle; // 中心角度（度）
        final float percentage;  // 占比
        final float r, g, b, a;

        ArcSection(float center, float percent, float r, float g, float b, float a) {
            this.centerAngle = center;
            this.percentage = percent;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }
}
