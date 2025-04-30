package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.event.StalkerManage;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import com.mafuyu404.diligentstalker.registry.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Tools {
    public static HashMap<String, Integer> ControlMap = new HashMap<>();

    private static void initControlMap() {
        ControlMap.put("Up", 0);
        ControlMap.put("Down", 0);
        ControlMap.put("Left", 0);
        ControlMap.put("Right", 0);
        ControlMap.put("Jump", 0);
        ControlMap.put("Shift", 0);
    }

    public static CompoundTag getEmptyInput() {
        if (ControlMap.isEmpty()) initControlMap();
        CompoundTag input = new CompoundTag();
        ControlMap.forEach((s, integer) -> {
            input.putBoolean(s, false);
        });
        return input;
    }

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
    public static float getXRotFromVec3(Vec3 vec) {
        return (float) Math.toDegrees(Math.asin(-vec.y));
    }
    public static float getYRotFromVec3(Vec3 vec) {
        return (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
    }

    public static Vec3 move(CompoundTag input, Vec3 motion) {
        float xRot = input.getFloat("xRot");
        float yRot = input.getFloat("yRot");
        float speed = 0.4f;
        Vec3 forward = Vec3.ZERO;
        Vec3 right = Vec3.ZERO;
        Vec3 top = Vec3.ZERO;
        Vec3 result = Vec3.ZERO;
        if (input.getBoolean("Up") || input.getBoolean("Down")) {
            float x = 0;
            float z = 0;
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
            forward = limitSpeed(new Vec3(x, 0, z), speed);
        }
        if (input.getBoolean("Left") || input.getBoolean("Right")) {
            float x = 0;
            float z = 0;
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
            right = limitSpeed(new Vec3(x, 0, z), speed);
        }
        if (input.getBoolean("Jump") || input.getBoolean("Shift")) {
            float y = 0;
            if (input.getBoolean("Jump")) {
                y = speed;
            }
            if (input.getBoolean("Shift")) {
                y = -speed;
            }
            top = limitSpeed(new Vec3(0, y, 0), speed);
        }

        result = result.add(forward).add(right).add(top);

        result = new Vec3(
                Mth.lerp(0.3f, motion.x, motion.x + result.x),
                Mth.lerp(0.3f, motion.y, motion.y + result.y),
                Mth.lerp(0.3f, motion.z, motion.z + result.z)
        );

        result = limitSpeed(new Vec3(result.x, 0, result.z), speed).add(limitSpeed(new Vec3(0, result.y, 0), speed));

        result = result.scale(0.8);

        return result;
    }
    public static Vec3 limitSpeed(Vec3 motion, float speed) {
        float length = (float) motion.length();
        if (Math.round(length * 100) / 100f > speed) {
            float scale = speed / length;
            motion = motion.scale(scale);
        }
        return motion;
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

    public static ArrayList<ChunkPos> getToLoadChunk(Entity stalker, int offset) {
        if (stalker == null) return new ArrayList<>();
        ChunkPos center = stalker.chunkPosition();
        ArrayList<ChunkPos> newChunks = new ArrayList<>();
        int radius = Config.RENDER_RADIUS_NORMAL.get() + offset;
        if (stalker instanceof VoidStalkerEntity) radius = Config.RENDER_RADIUS_SPECIAL.get();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                newChunks.add(new ChunkPos(center.x + x, center.z + z));
            }
        }
        return newChunks;
    }

    public static Map.Entry<String, BlockPos> entryOfUsingStalkerMaster(Player player) {
        if (player != null && player.isUsingItem()) {
            if (player.getMainHandItem().getItem() instanceof StalkerMasterItem) {
                CompoundTag tag = player.getMainHandItem().getOrCreateTag();
                if (tag.contains("StalkerId") && StalkerManage.DronePosition.containsKey(tag.getUUID("StalkerId"))) {
                    return StalkerManage.DronePosition.get(tag.getUUID("StalkerId"));
                }
            }
        }
        return null;
    }
    public static UUID uuidOfUsingStalkerMaster(Player player) {
        if (player != null && player.isUsingItem()) {
            if (player.getMainHandItem().getItem() instanceof StalkerMasterItem) {
                CompoundTag tag = player.getMainHandItem().getOrCreateTag();
                if (tag.contains("StalkerId")) {
                    return tag.getUUID("StalkerId");
                }
            }
        }
        return null;
    }
}
