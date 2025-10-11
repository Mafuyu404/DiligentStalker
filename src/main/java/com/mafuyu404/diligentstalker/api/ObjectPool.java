package com.mafuyu404.diligentstalker.api;

import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectPool {
    private static final ConcurrentLinkedQueue<ArrayList<ChunkPos>> CHUNK_LIST_POOL = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger CHUNK_LIST_POOL_SIZE = new AtomicInteger(0);
    
    private static final int MAX_POOL_SIZE = 1000;

    public static ArrayList<ChunkPos> getChunkPosList() {
        ArrayList<ChunkPos> list = CHUNK_LIST_POOL.poll();
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
            CHUNK_LIST_POOL_SIZE.decrementAndGet();
        }
        return list;
    }

    public static void returnChunkPosList(ArrayList<ChunkPos> list) {
        if (CHUNK_LIST_POOL_SIZE.get() < MAX_POOL_SIZE) {
            list.clear();
            CHUNK_LIST_POOL.offer(list);
            CHUNK_LIST_POOL_SIZE.incrementAndGet();
        }
    }
}