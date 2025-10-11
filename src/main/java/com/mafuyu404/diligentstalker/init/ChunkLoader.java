package com.mafuyu404.diligentstalker.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ChunkLoader {
    private static HashMap<ResourceLocation, ChunkLoader> dimension = new HashMap<>();
    private final ServerLevel level;
    private final Set<ChunkPos> forcedChunks = new HashSet<>();

    public ChunkLoader(ServerLevel level) {
        this.level = level;
    }

    public static ChunkLoader of(ServerLevel serverLevel) {
        ResourceLocation id = serverLevel.dimension().location();
        if (!dimension.containsKey(id)) dimension.put(id, new ChunkLoader(serverLevel));
        return dimension.get(id);
    }

    public static void init() {
        dimension = new HashMap<>();
    }

    public void addChunk(ChunkPos chunkPos) {
        if (!forcedChunks.contains(chunkPos)) {
            level.getChunkSource().addRegionTicket(
                    TicketType.FORCED,
                    chunkPos,
                    2, // 加载半径（0=单区块），不然会动不了
                    chunkPos // 标识
            );
            forcedChunks.add(chunkPos);
        }
    }

    public void removeAll() {
        for (ChunkPos chunkPos : forcedChunks) {
            level.getChunkSource().removeRegionTicket(
                    TicketType.FORCED,
                    chunkPos,
                    2,
                    chunkPos
            );
        }
        forcedChunks.clear();
    }
}
