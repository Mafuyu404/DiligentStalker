package com.mafuyu404.diligentstalker.init;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Tools {
    public static double lerp(double current, double target) {
        return current + (target - current) * 0.3;
    }

    public static Vec3 calculateViewVector(float xRot, float yRot) {
        float f = xRot * ((float)Math.PI / 180F);
        float f1 = -yRot * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static Vec3 move(CompoundTag input) {
        float xRot = input.getFloat("xRot");
        float yRot = input.getFloat("yRot");
        float x = 0;
        float y = 0;
        float z = 0;
        float speed = 0.5f;
        if (input.getBoolean("Up") || input.getBoolean("Down")) {
            Vec3 lookAngle = calculateViewVector(xRot, yRot);
            double xz = Math.sqrt(lookAngle.x * lookAngle.x + lookAngle.z * lookAngle.z);
            float forwardX = (float) (lookAngle.x / xz);
            float forwardZ = (float) (lookAngle.z / xz);
            if (input.getBoolean("Up")) {
                x += forwardX * speed;
                z += forwardZ * speed;
            }
            if (input.getBoolean("Down")) {
                x -= forwardX * speed;
                z -= forwardZ * speed;
            }
        }
        if (input.getBoolean("Left") || input.getBoolean("Right")) {
            Vec3 subAngle = Tools.calculateViewVector(xRot, yRot - 90);
            double xz = Math.sqrt(subAngle.x * subAngle.x + subAngle.z * subAngle.z);
            float forwardX = (float) (subAngle.x / xz);
            float forwardZ = (float) (subAngle.z / xz);
            if (input.getBoolean("Left")) {
                x += forwardX * speed;
                z += forwardZ * speed;
            }
            if (input.getBoolean("Right")) {
                x -= forwardX * speed;
                z -= forwardZ * speed;
            }
        }
        if (input.getBoolean("Jump")) {
            y = speed * 1.3f;
        }
        if (input.getBoolean("Shift")) {
            y = -speed * 1.3f;
        }
        float length = (float) new Vec3(x, 0, z).length();
        if (Math.round(length * 100) / 100f > speed) {
            float scale = length / speed;
            x /= scale;
            z /= scale;
        }
        return new Vec3(x, y, z);
    }

    public static double calculateViewAlignment(Vec3 target, Vec3 pos0, Vec3 pos1) {
        // 计算方向向量
        Vec3 direction = pos1.subtract(pos0);
        if (direction.length() < 1e-6) {
            return 0.0; // 避免零向量
        }
        Vec3 directionNormalized = direction.normalize();

        Vec3 viewVector = target.normalize();

        // 计算点积并转换为拟合度
        double dotProduct = directionNormalized.dot(viewVector);
        return (dotProduct + 1) / 2.0;
    }

    public static BlockHitResult rayTraceBlocks(Level world, Vec3 startPos, Vec3 direction, double maxDistance) {
        // 计算终点位置
        Vec3 endPos = startPos.add(direction.scale(maxDistance));

        // 创建ClipContext（视线追踪参数）
        ClipContext clipContext = new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.OUTLINE, // 检测方块轮廓
                ClipContext.Fluid.NONE,   // 忽略流体
                null                      // 不需要实体
        );

        // 执行射线追踪
        return world.clip(clipContext);
    }
}
