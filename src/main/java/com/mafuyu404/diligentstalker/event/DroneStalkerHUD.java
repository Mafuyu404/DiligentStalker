package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.StalkerUtil;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import com.mafuyu404.diligentstalker.registry.Config;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DroneStalkerHUD {
    private static int SIGNAL_RADIUS = 0;
    public static boolean RPress = false;

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            ResourceLocation icon = player.getSkinTextureLocation();
            ResourceLocation item = new ResourceLocation(DiligentStalker.MODID, "textures/entity/drone_stalker_forward.png");
            if (Stalker.hasInstanceOf(player)) {
                Entity stalker = Stalker.getInstanceOf(player).getStalker();
                Vec3 direction = stalker.position().subtract(player.position());
                float yRot = StalkerUtil.getYRotFromVec3(direction);
                int distance = (int) direction.length();
                if (stalker instanceof DroneStalkerEntity droneStalker) {
                    if (SIGNAL_RADIUS == 0) SIGNAL_RADIUS = Config.SIGNAL_RADIUS.get();

                    float signal_percent = 1 - (1f * distance / SIGNAL_RADIUS);
                    float fuel_percent = droneStalker.getFuel() / 100f;
                    List<ArcSection> sections = List.of(
                            new ArcSection(-157.5f, 0.375f, 0.7f, 0.7f, 0.7f, 0.4f),  // 左上灰色
                            new ArcSection(-157.5f - 0.375f * 180 * (1 - signal_percent), signal_percent * 0.375f, 0.8f, 0.8f, 0.8f, 1f),  // 左上灰色

                            new ArcSection(-22.5f, 0.375f, 0.6f, 0.8f, 1.0f, 0.4f),  // 右上淡蓝
                            new ArcSection(-22.5f + 0.375f * 180 * (1 - fuel_percent), fuel_percent * 0.375f, 0.6f, 0.8f, 1.0f, 1f),  // 右上淡蓝

                            new ArcSection(112.5f, 0.125f, 1.0f, 0.6f, 0.6f, 0.4f), // 左下淡红

                            new ArcSection(67.5f, 0.125f, 0.6f, 1.0f, 0.6f, 0.4f),  // 右下淡绿
                            new ArcSection(67.5f, RPress ? 0.125f : 0, 0.6f, 1.0f, 0.6f, 1f)  // 右下淡绿
                    );
                    drawHud(event.getGuiGraphics(), sections);
                }
                drawPlayerPosition(event.getGuiGraphics(), yRot - StalkerControl.yRot + 180, distance, icon);
            } else {
                ItemStack itemStack = player.getMainHandItem();
                if (itemStack.getItem() instanceof StalkerMasterItem) {
                    CompoundTag tag = itemStack.getOrCreateTag();
                    if (tag.contains("StalkerPosition")) {
                        int[] pos = tag.getIntArray("StalkerPosition");
                        Vec3 direction = new Vec3(pos[0], pos[1], pos[2]).subtract(player.position());
                        float yRot = StalkerUtil.getYRotFromVec3(direction);
                        int distance = (int) direction.length();
                        float signal_percent = 1 - (1f * distance / SIGNAL_RADIUS);
                        List<ArcSection> sections = List.of(
                                new ArcSection(-157.5f, 0.375f, 0.7f, 0.7f, 0.7f, 0.4f),
                                new ArcSection(-157.5f - 0.375f * 180 * (1 - signal_percent), signal_percent * 0.375f, 0.8f, 0.8f, 0.8f, 1f)
                        );
                        drawHud(event.getGuiGraphics(), sections);
                        drawPlayerPosition(event.getGuiGraphics(), yRot - player.getYRot(), distance, item);
                    }
                }
            }
        }
    }

    private static void drawHud(GuiGraphics guiGraphics, List<ArcSection> sections) {
        PoseStack poseStack = guiGraphics.pose();
        Window window = Minecraft.getInstance().getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        // 圆环基础参数
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        float radius = 80.0f;
        float thickness = 3.0f;

        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionShader);

        Tesselator tessellator = Tesselator.getInstance();

        // 总可用角度（360度 - 3个间隙）
        final float GAP = 2f;
        final float TOTAL_AVAILABLE = 360f - 3 * GAP;

        for (ArcSection section : sections) {
            // 计算实际角度范围
            float sectionAngle = TOTAL_AVAILABLE * section.percentage;

            // 计算起止角度（中心向两侧扩展）
            float start = section.centerAngle - sectionAngle / 2 + GAP / 2;
            float end = section.centerAngle + sectionAngle / 2 - GAP / 2;

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
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void drawPlayerPosition(GuiGraphics guiGraphics, float rotate, int distance, ResourceLocation icon) {
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        Window window = Minecraft.getInstance().getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        float radius = 90.0f; // 圆环半径

        // 计算头像位置
        double theta = Math.toRadians(90 - rotate);
        double xPos = centerX + radius * Math.cos(theta);
        double yPos = centerY - radius * Math.sin(theta);

        // 设置头像尺寸并居中
        int headSize = 12;
        int x = (int) (xPos - (double) headSize / 2);
        int y = (int) (yPos - (double) headSize / 2) - 2;

        // 渲染图标
        guiGraphics.blit(
                icon,
                x, y,
                headSize, headSize,
                8, 8,
                8, 8,
                64, 64
        );

        String text = distance + "m";
        Font font = Minecraft.getInstance().font;
        int textColor = 0xFFFFFFFF; // 白色
        int outlineColor = 0xFF000000; // 黑色
        int textWidth = font.width(text);
        int textX = x + (headSize - textWidth) / 2; // 水平居中
        int textY = y + headSize - 2;

        // 渲染文字描边（四周偏移1像素）
        guiGraphics.drawString(
                font,
                text,
                textX - 1, textY,
                outlineColor,
                false // 不启用阴影
        );
        guiGraphics.drawString(
                font,
                text,
                textX + 1, textY,
                outlineColor,
                false
        );
        guiGraphics.drawString(
                font,
                text,
                textX, textY - 1,
                outlineColor,
                false
        );
        guiGraphics.drawString(
                font,
                text,
                textX, textY + 1,
                outlineColor,
                false
        );

        // 渲染主体文字
        guiGraphics.drawString(
                font,
                text,
                textX, textY,
                textColor,
                false
        );
    }

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
