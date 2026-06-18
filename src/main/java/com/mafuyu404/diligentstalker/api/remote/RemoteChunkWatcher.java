package com.mafuyu404.diligentstalker.api.remote;

import com.mafuyu404.diligentstalker.api.IChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class RemoteChunkWatcher {
    private static final Map<UUID, Set<ChunkPos>> WATCHED_CHUNKS = new HashMap<>();

    private RemoteChunkWatcher() {
    }

    public static void update(ServerPlayer player, RemoteViewSession session) {
        if (player.level().dimension() != session.dimension()) {
            clear(player);
            return;
        }

        Set<ChunkPos> target = session.renderChunks();
        Set<ChunkPos> previous = WATCHED_CHUNKS.getOrDefault(player.getUUID(), Set.of());
        Set<ChunkPos> next = new HashSet<>(previous);
        IChunkMap chunkMap = (IChunkMap) player.serverLevel().getChunkSource().chunkMap;

        for (ChunkPos chunkPos : previous) {
            if (!target.contains(chunkPos)) {
                chunkMap.unwatchRemoteChunk(player, chunkPos);
                next.remove(chunkPos);
            }
        }

        for (ChunkPos chunkPos : target) {
            if (!previous.contains(chunkPos) && chunkMap.watchRemoteChunk(player, chunkPos)) {
                next.add(chunkPos);
            }
        }

        if (next.isEmpty()) {
            WATCHED_CHUNKS.remove(player.getUUID());
        } else {
            WATCHED_CHUNKS.put(player.getUUID(), next);
        }
    }

    public static void clear(ServerPlayer player) {
        Set<ChunkPos> previous = WATCHED_CHUNKS.remove(player.getUUID());
        if (previous == null || previous.isEmpty()) return;

        IChunkMap chunkMap = (IChunkMap) player.serverLevel().getChunkSource().chunkMap;
        for (ChunkPos chunkPos : previous) {
            chunkMap.unwatchRemoteChunk(player, chunkPos);
        }
    }

    public static boolean isRemoteWatched(ServerPlayer player, ChunkPos chunkPos) {
        return WATCHED_CHUNKS.getOrDefault(player.getUUID(), Set.of()).contains(chunkPos);
    }

}
