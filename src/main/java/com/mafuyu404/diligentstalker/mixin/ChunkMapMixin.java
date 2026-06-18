package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.api.IChunkMap;
import com.mafuyu404.diligentstalker.api.remote.RemoteChunkWatcher;
import com.mafuyu404.diligentstalker.api.remote.RemoteViewSession;
import com.mafuyu404.diligentstalker.api.remote.RemoteViewSessions;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = ChunkMap.class)
public abstract class ChunkMapMixin implements IChunkMap {
    @Shadow
    @Nullable
    protected abstract ChunkHolder getVisibleChunkIfPresent(long p_140328_);

    @Shadow
    protected abstract void playerLoadedChunk(ServerPlayer p_183761_, MutableObject<ClientboundLevelChunkWithLightPacket> p_183762_, LevelChunk p_183763_);

    @Shadow
    private int viewDistance;

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void wwaaa(ServerPlayer player, CallbackInfo ci) {
        RemoteViewSession session = RemoteViewSessions.get(player);
        if (session == null) {
            RemoteChunkWatcher.clear(player);
        }
    }

    public void loadLevelChunk(ServerPlayer player, ChunkPos chunkPos) {
        watchRemoteChunk(player, chunkPos);
    }

    public boolean watchRemoteChunk(ServerPlayer player, ChunkPos chunkPos) {
        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(chunkPos.toLong());
        if (chunkholder == null) return false;
        LevelChunk levelchunk = chunkholder.getTickingChunk();
        if (levelchunk == null) return false;
        this.playerLoadedChunk(player, new MutableObject<>(), levelchunk);
        return true;
    }

    public void unwatchRemoteChunk(ServerPlayer player, ChunkPos chunkPos) {
        player.untrackChunk(chunkPos);
        ForgeEventFactory.fireChunkUnWatch(player, chunkPos, (ServerLevel) player.level());
    }

    public void refreshPlayerChunks(ServerPlayer player) {
        ChunkPos center = player.chunkPosition();
        player.connection.send(new ClientboundSetChunkCacheCenterPacket(center.x, center.z));
        for (int x = center.x - this.viewDistance; x <= center.x + this.viewDistance; x++) {
            for (int z = center.z - this.viewDistance; z <= center.z + this.viewDistance; z++) {
                watchRemoteChunk(player, new ChunkPos(x, z));
            }
        }
    }
}
