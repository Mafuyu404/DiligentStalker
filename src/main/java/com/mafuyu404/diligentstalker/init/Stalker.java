package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.network.StalkerSyncPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Stalker {
    private final UUID playerUUID;
    private final int stalkerId;
    public final Level level;

    public static final ConcurrentHashMap<UUID, Integer> InstanceMap = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<UUID, Stalker> INSTANCE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Stalker> STALKER_CACHE = new ConcurrentHashMap<>();

    private static final long CACHE_EXPIRE_TIME = 5000;
    private long lastAccessTime;

    public Stalker(UUID playerUUID, int stalkerId, Level level) {
        this.playerUUID = playerUUID;
        this.stalkerId = stalkerId;
        this.level = level;
        this.lastAccessTime = System.currentTimeMillis();
    }

    public Player getPlayer() {
        this.lastAccessTime = System.currentTimeMillis();
        return level.getPlayerByUUID(playerUUID);
    }

    public Entity getStalker() {
        this.lastAccessTime = System.currentTimeMillis();
        return level.getEntity(stalkerId);
    }

    private static final ConcurrentHashMap<Integer, UUID> STALKER_TO_PLAYER = new ConcurrentHashMap<>();

    public static Stalker connect(Player player, Entity stalker) {
        if (player == null || stalker == null) return null;
        if (hasInstanceOf(player) || hasInstanceOf(stalker)) return null;

        if (player.level().isClientSide) {
            StalkerControl.connect(player, stalker);
            NetworkHandler.sendToServer(new StalkerSyncPacket(stalker.getId(), true));
        }

        InstanceMap.put(player.getUUID(), stalker.getId());
        STALKER_TO_PLAYER.put(stalker.getId(), player.getUUID());  // 添加反向索引

        Stalker instance = new Stalker(player.getUUID(), stalker.getId(), player.level());
        INSTANCE_CACHE.put(player.getUUID(), instance);
        STALKER_CACHE.put(stalker.getId(), instance);

        return instance;
    }

    public static Stalker getInstanceOf(Entity entity) {
        if (entity == null) return null;

        UUID uuid = entity.getUUID();
        int id = entity.getId();

        // 先检查缓存
        Stalker cached = INSTANCE_CACHE.get(uuid);
        if (cached != null && !isCacheExpired(cached)) {
            return cached;
        }

        cached = STALKER_CACHE.get(id);
        if (cached != null && !isCacheExpired(cached)) {
            return cached;
        }

        // 使用 O(1) 查找替代 O(n) 遍历
        boolean isPlayer = InstanceMap.containsKey(uuid);
        boolean isStalker = STALKER_TO_PLAYER.containsKey(id);  // O(1) 操作

        Stalker instance = null;
        if (isPlayer) {
            Integer stalkerId = InstanceMap.get(uuid);
            if (stalkerId != null) {
                instance = new Stalker(uuid, stalkerId, entity.level());
                INSTANCE_CACHE.put(uuid, instance);
            }
        } else if (isStalker) {
            UUID playerUUID = STALKER_TO_PLAYER.get(id);  // O(1) 操作
            if (playerUUID != null) {
                instance = new Stalker(playerUUID, id, entity.level());
                STALKER_CACHE.put(id, instance);
            }
        }

        return instance;
    }

    public void disconnect() {
        if (level.isClientSide) {
            NetworkHandler.sendToServer(new StalkerSyncPacket(this.stalkerId, false));
        }
        InstanceMap.remove(playerUUID);
        STALKER_TO_PLAYER.remove(stalkerId);  // 清理反向索引
        INSTANCE_CACHE.remove(playerUUID);
        STALKER_CACHE.remove(stalkerId);
    }

    public static boolean hasInstanceOf(Entity entity) {
        if (entity == null) return false;

        UUID uuid = entity.getUUID();
        if (INSTANCE_CACHE.containsKey(uuid) && !isCacheExpired(INSTANCE_CACHE.get(uuid))) {
            return true;
        }

        int id = entity.getId();
        if (STALKER_CACHE.containsKey(id) && !isCacheExpired(STALKER_CACHE.get(id))) {
            return true;
        }

        boolean isPlayer = InstanceMap.containsKey(uuid);
        boolean isStalker = InstanceMap.containsValue(id);
        return (isPlayer || isStalker);
    }

    private static boolean isCacheExpired(Stalker instance) {
        return System.currentTimeMillis() - instance.lastAccessTime > CACHE_EXPIRE_TIME;
    }

    public static void cleanupExpiredCache() {
        long currentTime = System.currentTimeMillis();
        INSTANCE_CACHE.entrySet().removeIf(entry ->
                currentTime - entry.getValue().lastAccessTime > CACHE_EXPIRE_TIME);
        STALKER_CACHE.entrySet().removeIf(entry ->
                currentTime - entry.getValue().lastAccessTime > CACHE_EXPIRE_TIME);
    }
}

