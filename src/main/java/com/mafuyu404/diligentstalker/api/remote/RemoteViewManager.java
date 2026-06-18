package com.mafuyu404.diligentstalker.api.remote;

import com.mafuyu404.diligentstalker.api.IChunkMap;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public final class RemoteViewManager {
    private RemoteViewManager() {
    }

    public static void update(ServerPlayer player) {
        RemoteViewSession session = RemoteViewSessions.get(player);
        if (session != null) {
            RemoteChunkWatcher.update(player, session);
        } else {
            RemoteChunkWatcher.clear(player);
        }
    }

    public static void restorePlayerView(ServerPlayer player) {
        ServerStalkerUtil.setVisualCenter(player, BlockPos.ZERO);
        RemoteChunkWatcher.clear(player);
        player.getPersistentData().putBoolean("LoadingCacheChunk", false);
        player.serverLevel().getChunkSource().move(player);
        ((IChunkMap) player.serverLevel().getChunkSource().chunkMap).refreshPlayerChunks(player);
    }
}
