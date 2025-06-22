package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.api.ObjectPool;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.event.StalkerManage;
import com.mafuyu404.diligentstalker.item.StalkerMasterItem;
import com.mafuyu404.diligentstalker.registry.ModConfig;
import net.minecraft.client.Minecraft;
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
import java.util.concurrent.ConcurrentHashMap;

public class Tools {
    public static HashMap<String, Integer> ControlMap = new HashMap<>();

    private static final ConcurrentHashMap<String, Vec3> VIEW_VECTOR_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Double> LERP_CACHE = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRE_TIME = 1000;
    private static long lastCacheCleanup = 0;

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


    /**
     * 视角向量计算（使用缓存和对象池）
     */
    public static Vec3 calculateViewVector(float xRot, float yRot) {
        String key = Math.round(xRot * 100) / 100f + "," + Math.round(yRot * 100) / 100f;

        return VIEW_VECTOR_CACHE.computeIfAbsent(key, k -> {
            float f = xRot * ((float)Math.PI / 180F);
            float f1 = -yRot * ((float)Math.PI / 180F);
            float f2 = Mth.cos(f1);
            float f3 = Mth.sin(f1);
            float f4 = Mth.cos(f);
            float f5 = Mth.sin(f);
            return new Vec3(f3 * f4, -f5, f2 * f4);
        });
    }

    public static float getXRotFromVec3(Vec3 vec) {
        return (float) Math.toDegrees(Math.asin(-vec.y));
    }

    public static float getYRotFromVec3(Vec3 vec) {
        return (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
    }

    /**
     * 移动计算（使用可变向量减少对象创建）
     */
    public static Vec3 move(CompoundTag input, Vec3 motion) {
        float xRot = input.getFloat("xRot");
        float yRot = input.getFloat("yRot");
        float speed = 0.45f;

        ObjectPool.MutableVec3 forward = ObjectPool.getMutableVec3().set(0, 0, 0);
        ObjectPool.MutableVec3 right = ObjectPool.getMutableVec3().set(0, 0, 0);
        ObjectPool.MutableVec3 top = ObjectPool.getMutableVec3().set(0, 0, 0);
        ObjectPool.MutableVec3 result = ObjectPool.getMutableVec3().set(0, 0, 0);

        try {
            if (input.getBoolean("Up") || input.getBoolean("Down")) {
                Vec3 lookAngle = calculateViewVector(xRot, yRot);
                double xz = Math.sqrt(lookAngle.x * lookAngle.x + lookAngle.z * lookAngle.z);
                float forwardX = (float) (lookAngle.x / xz);
                float forwardZ = (float) (lookAngle.z / xz);

                float x = 0, z = 0;
                if (input.getBoolean("Up")) {
                    x += forwardX * speed;
                    z += forwardZ * speed;
                }
                if (input.getBoolean("Down")) {
                    x -= forwardX * speed;
                    z -= forwardZ * speed;
                }
                forward.set(x, 0, z);
                limitSpeedMutable(forward, speed);
            }

            if (input.getBoolean("Left") || input.getBoolean("Right")) {
                Vec3 subAngle = calculateViewVector(xRot, yRot - 90);
                double xz = Math.sqrt(subAngle.x * subAngle.x + subAngle.z * subAngle.z);
                float forwardX = (float) (subAngle.x / xz);
                float forwardZ = (float) (subAngle.z / xz);

                float x = 0, z = 0;
                if (input.getBoolean("Left")) {
                    x += forwardX * speed;
                    z += forwardZ * speed;
                }
                if (input.getBoolean("Right")) {
                    x -= forwardX * speed;
                    z -= forwardZ * speed;
                }
                right.set(x, 0, z);
                limitSpeedMutable(right, speed);
            }

            if (input.getBoolean("Jump") || input.getBoolean("Shift")) {
                float y = 0;
                if (input.getBoolean("Jump")) {
                    y = speed;
                }
                if (input.getBoolean("Shift")) {
                    y = -speed;
                }
                top.set(0, y, 0);
                limitSpeedMutable(top, speed);
            }

            result.add(forward).add(right).add(top);

            result.set(
                    Mth.lerp(0.3f, motion.x, motion.x + result.x),
                    Mth.lerp(0.3f, motion.y, motion.y + result.y),
                    Mth.lerp(0.3f, motion.z, motion.z + result.z)
            );

            ObjectPool.MutableVec3 horizontal = ObjectPool.getMutableVec3().set(result.x, 0, result.z);
            ObjectPool.MutableVec3 vertical = ObjectPool.getMutableVec3().set(0, result.y, 0);

            try {
                limitSpeedMutable(horizontal, speed);
                limitSpeedMutable(vertical, speed);
                result.set(horizontal.x + vertical.x, horizontal.y + vertical.y, horizontal.z + vertical.z);

                result.x *= 0.8;
                result.y *= 0.8;
                result.z *= 0.8;

                return result.toVec3();
            } finally {
                ObjectPool.returnMutableVec3(horizontal);
                ObjectPool.returnMutableVec3(vertical);
            }
        } finally {
            ObjectPool.returnMutableVec3(forward);
            ObjectPool.returnMutableVec3(right);
            ObjectPool.returnMutableVec3(top);
            ObjectPool.returnMutableVec3(result);
        }
    }

    /**
     * 可变向量的速度限制
     */
    private static void limitSpeedMutable(ObjectPool.MutableVec3 motion, float speed) {
        double length = motion.length();
        if (Math.round(length * 100) / 100f > speed) {
            double scale = speed / length;
            motion.x *= scale;
            motion.y *= scale;
            motion.z *= scale;
        }
    }


    /**
     * 视角对齐计算
     */
    public static double calculateViewAlignment(Vec3 target, Vec3 start, Vec3 end) {
        ObjectPool.MutableVec3 direction = ObjectPool.getMutableVec3().set(end).subtract(ObjectPool.getMutableVec3().set(start));

        try {
            if (direction.length() < 1e-6) {
                return 0.0; // 避免零向量
            }
            direction.normalize();

            ObjectPool.MutableVec3 viewVector = ObjectPool.getMutableVec3().set(target).normalize();

            try {
                // 计算点积并转换为拟合度
                double dotProduct = direction.x * viewVector.x + direction.y * viewVector.y + direction.z * viewVector.z;
                return (dotProduct + 1) / 2.0;
            } finally {
                ObjectPool.returnMutableVec3(viewVector);
            }
        } finally {
            ObjectPool.returnMutableVec3(direction);
        }
    }

    public static BlockHitResult rayTraceBlocks(Level world, Vec3 startPos, Vec3 direction, double maxDistance) {
        // 计算终点位置
        Vec3 endPos = startPos.add(direction.scale(maxDistance));

        ClipContext clipContext = new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.OUTLINE, // 检测方块轮廓
                ClipContext.Fluid.NONE,   // 忽略流体
                Minecraft.getInstance().player
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

    /**
     * 定期清理缓存
     */
    public static void cleanupCache() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheCleanup > CACHE_EXPIRE_TIME) {
            VIEW_VECTOR_CACHE.clear();
            LERP_CACHE.clear();
            lastCacheCleanup = currentTime;
        }
    }
}
