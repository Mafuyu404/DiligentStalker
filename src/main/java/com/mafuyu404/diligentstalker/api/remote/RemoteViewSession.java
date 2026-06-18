package com.mafuyu404.diligentstalker.api.remote;

import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Set;

public interface RemoteViewSession {
    ServerPlayer player();

    ResourceKey<Level> dimension();

    BlockPos centerBlock();

    default ChunkPos centerChunk() {
        return new ChunkPos(centerBlock());
    }

    int loadRadius();

    int renderRadius();

    default Set<ChunkPos> loadChunks() {
        return StalkerUtil.getChunksAround(centerChunk(), loadRadius());
    }

    default Set<ChunkPos> renderChunks() {
        return StalkerUtil.getChunksAround(centerChunk(), renderRadius());
    }

    RemoteViewMode mode();

    @Nullable
    Entity stalkerEntity();
}
