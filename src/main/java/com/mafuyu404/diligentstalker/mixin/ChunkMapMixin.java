package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.api.IChunkMap;
import com.mafuyu404.diligentstalker.init.ServerStalkerUtil;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
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

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void wwaaa(ServerPlayer player, CallbackInfo ci) {
        if (ServerStalkerUtil.hasVisualCenter(player)) {
            loadLevelChunk(player, new ChunkPos(ServerStalkerUtil.getVisualCenter(player)));
        }
        ChunkPos center = null;
        if (Stalker.hasInstanceOf(player)) {
            center = Stalker.getInstanceOf(player).getStalker().chunkPosition();
        }
        if (player.getPersistentData().getBoolean("LoadingCacheChunk")) {
            center = player.chunkPosition();
            player.getPersistentData().putBoolean("LoadingCacheChunk", false);
        }
        if (center == null) return;
        int radius = Config.RENDER_RADIUS_NORMAL.get();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                loadLevelChunk(player, new ChunkPos(center.x + x, center.z + z));
            }
        }
        ci.cancel();
    }

    public void loadLevelChunk(ServerPlayer player, ChunkPos chunkPos) {
        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(chunkPos.toLong());
        if (chunkholder == null) return;
        LevelChunk levelchunk = chunkholder.getTickingChunk();
        if (levelchunk == null) return;
        this.playerLoadedChunk(player, new MutableObject<>(), levelchunk);
    }
}
