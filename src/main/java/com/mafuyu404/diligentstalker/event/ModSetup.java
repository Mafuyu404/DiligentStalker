package com.mafuyu404.diligentstalker.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

public class ModSetup {
    // 存储实体UUID和它们强制加载的区块
    private static final Map<UUID, Set<ChunkPos>> ENTITY_CHUNKS = new HashMap<>();

    public static void init() {
        // 注册服务器启动事件
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // 服务器启动时的初始化
        });

        // 注册服务器tick事件，用于验证区块票据
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                validateChunkTickets(level);
            }
        });
    }

    private static void validateChunkTickets(ServerLevel level) {
        // 创建一个需要移除的实体列表
        Set<UUID> toRemove = new HashSet<>();

        // 检查每个实体的区块票据
        ENTITY_CHUNKS.forEach((uuid, chunks) -> {
            if (level.getEntity(uuid) == null) {
                // 如果实体不存在，将其添加到待移除列表
                toRemove.add(uuid);

                // 取消该实体的所有区块强制加载
                for (ChunkPos pos : chunks) {
                    level.setChunkForced(pos.x, pos.z, false);
                }
            }
        });

        // 从映射中移除不存在的实体
        toRemove.forEach(ENTITY_CHUNKS::remove);
    }

}
