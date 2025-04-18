package com.mafuyu404.diligentstalker.init;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityCameraHandler {
    private static Entity targetEntity;
    public static boolean isAttached = false;

    public static void attachToEntity(Entity entity) {
        targetEntity = entity;
        isAttached = true;
        // 可选：禁用玩家输入
        Minecraft.getInstance().options.hideGui = true;
    }

    public static void detach() {
        targetEntity = null;
        isAttached = false;
        Minecraft.getInstance().options.hideGui = false;
    }

    // 在客户端Tick事件中调用
    public static void updateCamera() {
        if (isAttached && targetEntity != null) {
            Minecraft mc = Minecraft.getInstance();
            Entity viewEntity = mc.getCameraEntity();

            // 保留玩家实际坐标
            Vec3 actualPos = viewEntity.position();

            // 获取目标实体视角参数
            float yaw = targetEntity.getYRot();
            float pitch = targetEntity.getXRot();
            Vec3 targetPos = targetEntity.getEyePosition();

            // 设置摄像机位置（混合实际坐标和目标坐标）
//            mc.gameRenderer.getMainCamera().(
//                    actualPos.x + (targetPos.x - actualPos.x) * 0.5,
//                    actualPos.y + (targetPos.y - actualPos.y) * 0.5,
//                    actualPos.z + (targetPos.z - actualPos.z) * 0.5
//            );

            // 平滑插值旋转
//            viewEntity.setYRot(lerpRotation(viewEntity.getYRot(), yaw));
//            viewEntity.setXRot(lerpRotation(viewEntity.getXRot(), pitch));
        }
    }

    private static float lerpRotation(float current, float target) {
        return current + (target - current) * 0.3f;
    }
}
