package com.mafuyu404.diligentstalker.init;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ChunkLoader {
    // 存储每个维度中加载的区块
    private static final HashMap<String, ArrayList<ChunkPos>> loaders = new HashMap<>();

    public static void add(ServerLevel level, ChunkPos center) {
        String key = level.dimension().toString();
        if (!loaders.containsKey(key)) loaders.put(key, new ArrayList<>());
        if (!loaders.get(key).contains(center)) {
            loaders.get(key).add(center);
            level.setChunkForced(center.x, center.z, true);
        }
    }

    public static void removeAll(ServerLevel level) {
        String key = level.dimension().toString();
        if (!loaders.containsKey(key)) return;
        if (loaders.get(key).isEmpty()) return;
        Iterator<ChunkPos> iterator = loaders.get(key).iterator();
        while (iterator.hasNext()) {
            ChunkPos center = iterator.next();
            iterator.remove();
            level.setChunkForced(center.x, center.z, false);
        }
    }
}