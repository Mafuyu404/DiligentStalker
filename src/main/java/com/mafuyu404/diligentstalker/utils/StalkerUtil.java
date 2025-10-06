package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.registry.ModConfig;
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
        return current + (target - current) * 0.2;
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

    public static BlockHitResult rayTraceBlocks(Level world, Entity actor, Vec3 startPos, Vec3 direction, double maxDistance) {
        Vec3 endPos = startPos.add(direction.scale(maxDistance));

        ClipContext clipContext = new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                actor // 传入非空实体（如玩家）
        );

        return world.clip(clipContext);
    }

    // 使用对象池减少内存分配
    private static final ConcurrentHashMap<String, ArrayList<ChunkPos>> CHUNK_CACHE = new ConcurrentHashMap<>();
    private static final long CHUNK_CACHE_EXPIRE = 100; // 100ms 缓存
    private static long lastChunkCacheCleanup = 0;

    public static ArrayList<ChunkPos> getToLoadChunks(Entity stalker, int offset) {
        if (stalker == null) return new ArrayList<>();

        ChunkPos center = stalker.chunkPosition();
        int radius = ModConfig.getRenderRadiusNormal() + offset;
        if (stalker instanceof VoidStalkerEntity) radius = ModConfig.getRenderRadiusSpecial();

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
