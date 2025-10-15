package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.api.IChunkMap;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.registry.Config;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.EventHooks;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements IChunkMap {
    @Shadow
    @Nullable
    public abstract ChunkHolder getVisibleChunkIfPresent(long chunkPos);

    @Shadow
    private void applyChunkTrackingView(ServerPlayer player, ChunkTrackingView view) {}
    @Shadow
    public abstract int getPlayerViewDistance(ServerPlayer player);
    @Shadow
    public abstract void waitForLightBeforeSending(ChunkPos chunkPos, int range);

    private static final Logger DS_LOGGER = LogUtils.getLogger();

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void diligentstalker$overrideMove(ServerPlayer player, CallbackInfo ci) {
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
        ci.cancel();
    }

    @Inject(method = "updateChunkTracking", at = @At("HEAD"), cancellable = true)
    private void ds$updateChunkTracking(ServerPlayer player, CallbackInfo ci) {
        boolean hasStalkerConnection = Stalker.hasInstanceOf(player);
        ChunkPos stalkerPos = null;
        if (hasStalkerConnection) {
            stalkerPos = Stalker.getInstanceOf(player).getStalker().chunkPosition();
        }

        if (hasStalkerConnection && stalkerPos != null) {
            int viewDistance = getPlayerViewDistance(player);
            waitForLightBeforeSending(stalkerPos, viewDistance);
            this.applyChunkTrackingView(player, ChunkTrackingView.of(stalkerPos, viewDistance));
            ci.cancel();
        }
    }

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

        boolean loadedVisible = this.getVisibleChunkIfPresent(posLong) != null;

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
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public void loadLevelChunk(ServerPlayer player, ChunkPos chunkPos) {
        ChunkHolder holder = this.getVisibleChunkIfPresent(chunkPos.toLong());
        if (holder == null) return;

        LevelChunk levelChunk = holder.getTickingChunk();
        if (levelChunk == null) return;

        player.connection.chunkSender.markChunkPendingToSend(levelChunk);
        EventHooks.fireChunkWatch(player, levelChunk, player.serverLevel());
    }
}