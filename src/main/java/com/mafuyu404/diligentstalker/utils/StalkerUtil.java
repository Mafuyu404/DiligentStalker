package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.registry.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StalkerUtil {
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
        float f = xRot * ((float) Math.PI / 180F);
        float f1 = -yRot * ((float) Math.PI / 180F);
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
        float speed = 0.45f;
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
            Vec3 subAngle = StalkerUtil.calculateViewVector(xRot, yRot - 90);
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

    public static double calculateViewAlignment(Vec3 target, Vec3 start, Vec3 end) {
        // 计算方向向量
        Vec3 direction = end.subtract(start);
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

    // 使用对象池减少内存分配
    private static final ConcurrentHashMap<String, ArrayList<ChunkPos>> CHUNK_CACHE = new ConcurrentHashMap<>();
    private static final long CHUNK_CACHE_EXPIRE = 100; // 100ms 缓存
    private static long lastChunkCacheCleanup = 0;

    public static ArrayList<ChunkPos> getToLoadChunks(Entity stalker, int offset) {
        if (stalker == null) return new ArrayList<>();

        ChunkPos center = stalker.chunkPosition();
        int radius = Config.RENDER_RADIUS_NORMAL.get() + offset;
        if (stalker instanceof VoidStalkerEntity) radius = Config.RENDER_RADIUS_SPECIAL.get();

        // 使用缓存键
        String cacheKey = center.x + "," + center.z + "," + radius;

        // 检查缓存
        ArrayList<ChunkPos> cached = CHUNK_CACHE.get(cacheKey);
        if (cached != null) {
            return new ArrayList<>(cached); // 返回副本避免并发修改
        }

        // 预分配容量减少扩容
        int expectedSize = (radius * 2 + 1) * (radius * 2 + 1);
        ArrayList<ChunkPos> newChunks = new ArrayList<>(expectedSize);

        int radiusSquared = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distanceSquared = x * x + z * z;
                if (distanceSquared <= radiusSquared || radius < 5) {
                    newChunks.add(new ChunkPos(center.x + x, center.z + z));
                }
            }
        }

        // 缓存结果
        CHUNK_CACHE.put(cacheKey, new ArrayList<>(newChunks));

        // 定期清理缓存
        cleanupChunkCache();

        return newChunks;
    }

    private static void cleanupChunkCache() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastChunkCacheCleanup > CHUNK_CACHE_EXPIRE) {
            CHUNK_CACHE.clear();
            lastChunkCacheCleanup = currentTime;
        }
    }
}
