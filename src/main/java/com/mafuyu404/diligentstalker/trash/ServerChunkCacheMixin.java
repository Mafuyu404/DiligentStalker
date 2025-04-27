package com.mafuyu404.diligentstalker.trash;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Shadow public abstract void setViewDistance(int p_8355_);

    @Inject(
            method = "updateChunkForced",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onUpdateChunkForced(ChunkPos pos, boolean add, CallbackInfo ci) {
        // 处理玩家和无人机的区块加载
//        if (StalkerManage.getCameraEntity(player) != null) {
//            this.setViewDistance(10);
//        }
//        for (Entity entity : this.level.getEntities().getAll()) {
//            if (entity instanceof Player || entity instanceof DroneStalkerEntity) {
//                ChunkPos entityChunk = entity.chunkPosition();
//                int radius = entity instanceof DroneStalkerEntity ? 5 : 0;
//
//                if (Math.abs(pos.x - entityChunk.x) <= radius &&
//                        Math.abs(pos.z - entityChunk.z) <= radius) {
//                    ci.cancel(); // 阻止原版卸载逻辑
//                    return;
//                }
//            }
//        }
    }
}
