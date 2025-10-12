package com.mafuyu404.diligentstalker.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public interface IChunkMap {
    void loadLevelChunk(ServerPlayer player, ChunkPos chunkPos);
}
