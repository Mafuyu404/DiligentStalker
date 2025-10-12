package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public abstract class TrackedEntityMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    @Final
    Entity entity;

    @Shadow
    @Final
    ChunkMap this$0;

    @Shadow public abstract int getEffectiveRange();

    @Inject(method = "updatePlayer", at = @At("HEAD"))
    private void logUpdatePlayer(ServerPlayer player, CallbackInfo ci) {
        if (player == this.entity) return;

        if (entity instanceof DroneStalkerEntity || Stalker.hasInstanceOf(entity)) {
            Vec3 playerPos = player.position();
            Vec3 entityPos = entity.position();
            Vec3 vec3 = playerPos.subtract(entityPos);

            int viewDistance = this$0.getPlayerViewDistance(player);
            double effectiveRange = Math.min(this.getEffectiveRange(), viewDistance * 16);
            double distanceSq = vec3.x * vec3.x + vec3.z * vec3.z;
            boolean broadcast = entity.broadcastToPlayer(player);
            boolean isTracked = this$0.isChunkTracked(player, entity.chunkPosition().x, entity.chunkPosition().z);
            boolean flag = distanceSq <= effectiveRange * effectiveRange && broadcast && isTracked;

//            LOGGER.warn("[updatePlayer] Check tracking: player={} (id={}), entity={} (id={}), distanceSq={}, effectiveRangeSq={}, broadcast={}, isChunkTracked={}, willRemove={}",
//                    player.getName().getString(),
//                    player.getId(),
//                    entity.getClass().getSimpleName(),
//                    entity.getId(),
//                    distanceSq,
//                    effectiveRange * effectiveRange,
//                    broadcast,
//                    isTracked,
//                    !flag
//            );
        }
    }
}
