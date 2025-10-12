package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.api.IChunkMap;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.Config;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.*;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mixin(value = ChunkMap.class)
public abstract class ChunkMapMixin implements IChunkMap {
    @Shadow
    @Nullable
    public abstract ChunkHolder getVisibleChunkIfPresent(long l);

    @Shadow
    public abstract LevelChunk getChunkToSend(long l);

    @Shadow private void applyChunkTrackingView(ServerPlayer player, ChunkTrackingView view) {}
    @Shadow public abstract int getPlayerViewDistance(ServerPlayer player);
    @Shadow public abstract void waitForLightBeforeSending(ChunkPos chunkPos, int range);

    private static final Logger DS_LOGGER = LogUtils.getLogger();

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void wwaaa(ServerPlayer player, CallbackInfo ci) {
        boolean hasStalkerConnection = Stalker.hasInstanceOf(player);
        ChunkPos stalkerPos = null;
        if (hasStalkerConnection) {
            stalkerPos = Stalker.getInstanceOf(player).getStalker().chunkPosition();
        }

        if (ServerStalkerUtil.hasVisualCenter(player)) {
            loadLevelChunk(player, new ChunkPos(ServerStalkerUtil.getVisualCenter(player)));
        }
        
        ChunkPos center = null;
        if (hasStalkerConnection) {
            center = stalkerPos;
            ci.cancel();
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
    }

    // 当玩家连接到跟踪狂时，以跟踪狂位置更新追踪视图
    @Inject(method = "updateChunkTracking", at = @At("HEAD"), cancellable = true)
    private void ds$updateChunkTracking(ServerPlayer player, CallbackInfo ci) {
        boolean hasStalkerConnection = Stalker.hasInstanceOf(player);
        ChunkPos stalkerPos = null;
        if (hasStalkerConnection) {
            stalkerPos = Stalker.getInstanceOf(player).getStalker().chunkPosition();
        }
        
        if (hasStalkerConnection && stalkerPos != null) {
            int viewDistance = getPlayerViewDistance(player);
            // 确保光照依赖先完成，减少 pending 时长（似乎可有可无）
            waitForLightBeforeSending(stalkerPos, viewDistance);
            this.applyChunkTrackingView(player, ChunkTrackingView.of(stalkerPos, viewDistance));
            ci.cancel();
        }
    }

    // 判定时也围绕跟踪狂位置（与 updateChunkTracking 保持一致）
    @Inject(method = "isChunkTracked", at = @At("HEAD"), cancellable = true)
    private void ds$isChunkTracked(ServerPlayer player, int x, int z, CallbackInfoReturnable<Boolean> cir) {
        int viewDistance = getPlayerViewDistance(player);
        boolean originalContains = player.getChunkTrackingView().contains(x, z);
        long posLong = ChunkPos.asLong(x, z);
        boolean pending = player.connection.chunkSender.isPending(posLong);

        boolean hasStalkerConnection = Stalker.hasInstanceOf(player);
        ChunkPos stalkerPos = null;
        boolean containsStalkerView = false;
        
        if (hasStalkerConnection) {
            stalkerPos = Stalker.getInstanceOf(player).getStalker().chunkPosition();
            containsStalkerView = ChunkTrackingView.isWithinDistance(stalkerPos.x, stalkerPos.z, viewDistance, x, z, true);
        }

        // 若区块已在可见缓存（已加载到 ChunkHolder），则不因 pending 视为未追踪，避免刚连上就断开
        boolean loadedVisible = this.getVisibleChunkIfPresent(posLong) != null;

        // 有跟踪狂连接时以跟踪狂位置为准；没有连接时，originalContains 且（非 pending 或已可见）
        boolean decision = hasStalkerConnection ? containsStalkerView : (originalContains && (!pending || loadedVisible));

        if (!decision) {
            String reason;
            if (hasStalkerConnection) {
                reason = "outside_stalker_view";
            } else if (!originalContains) {
                reason = "outside_player_view";
            } else {
                reason = "pending_chunk";
            }
            DS_LOGGER.info(
                "[DS][isChunkTracked=false] player={} x={} z={} reason={} originalContains={} pending={} hasStalkerConnection={} stalkerPos={} containsStalkerView={} viewDistance={} loadedVisible={}",
                player.getGameProfile().getName(), x, z,
                reason, originalContains, pending,
                hasStalkerConnection, stalkerPos, containsStalkerView, viewDistance, loadedVisible
            );
        } else {
            // 返回 true 的路径直接覆盖结果，避免后续原生逻辑把 pending 视为未追踪
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    public void loadLevelChunk(ServerPlayer player, ChunkPos chunkPos) {
        LevelChunk levelchunk = this.getChunkToSend(chunkPos.toLong());
        if (levelchunk == null) return;

        player.connection.chunkSender.markChunkPendingToSend(levelchunk);
    }
}