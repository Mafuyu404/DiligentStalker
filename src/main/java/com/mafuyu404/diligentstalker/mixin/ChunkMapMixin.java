package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.api.IChunkMap;
import com.mafuyu404.diligentstalker.component.ModComponents;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.ModConfig;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
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
    protected abstract ChunkHolder getVisibleChunkIfPresent(long l);

    @Shadow
    public abstract LevelChunk getChunkToSend(long l);

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void wwaaa(ServerPlayer player, CallbackInfo ci) {
        if (ServerStalkerUtil.hasVisualCenter(player)) {
            loadLevelChunk(player, new ChunkPos(ServerStalkerUtil.getVisualCenter(player)));
        }
        ChunkPos center = null;
        if (Stalker.hasInstanceOf(player)) {
            center = Stalker.getInstanceOf(player).getStalker().chunkPosition();
        }
        if (ModComponents.STALKER_DATA.get(player).getStalkerData().getBoolean("LoadingCacheChunk")) {
            center = player.chunkPosition();
            ModComponents.STALKER_DATA.get(player).getStalkerData().putBoolean("LoadingCacheChunk", false);
        }
        if (center == null) return;
        int radius = ModConfig.getRenderRadiusNormal();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                loadLevelChunk(player, new ChunkPos(center.x + x, center.z + z));
            }
        }
        ci.cancel();
    }

    //TODO 区块加载嫌疑1号
    public void loadLevelChunk(ServerPlayer player, ChunkPos chunkPos) {
        LevelChunk levelchunk = this.getChunkToSend(chunkPos.toLong());
        if (levelchunk == null) return;
        player.connection.chunkSender.markChunkPendingToSend(levelchunk);
    }
}