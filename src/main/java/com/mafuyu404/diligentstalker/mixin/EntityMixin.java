package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.CameraEntityManage;
import com.mafuyu404.diligentstalker.event.ServerStalker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.UUID;

@Mixin(value = Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @Shadow private Level level;

    @Shadow private ChunkPos chunkPosition;

    @Inject(method = "setXRot", at = @At("HEAD"), cancellable = true)
    private void redirectXRot(float xRot, CallbackInfo ci) {
        if (((Object) this) instanceof Player player) {
            if (!player.isLocalPlayer()) return;
            if (!CameraEntityManage.isEnable(player)) return;
            CameraEntityManage.xRot += xRot - CameraEntityManage.fixedXRot;
            ci.cancel();
        }
    }
    @Inject(method = "setYRot", at = @At("HEAD"), cancellable = true)
    private void redirectYRot(float yRot, CallbackInfo ci) {
        if (((Object) this) instanceof Player player) {
            if (!player.isLocalPlayer()) return;
            if (!CameraEntityManage.isEnable(player)) return;
            CameraEntityManage.yRot += yRot - CameraEntityManage.fixedYRot;
            ci.cancel();
        }
    }

    @Inject(method = "distanceToSqr(DDD)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistance(double p_20276_, double p_20277_, double p_20278_, CallbackInfoReturnable<Double> cir) {
        if (((Object) this) instanceof Player player) {
            if (CameraEntityManage.isEnable(player)) {
                cir.setReturnValue(1d);
            }
        }
    }
    @Inject(method = "setPosRaw", at = @At("HEAD"), cancellable = true)
    private void wwaaaa(double p_20210_, double p_20211_, double p_20212_, CallbackInfo ci) {
        if (((Object) this) instanceof Player player) {
            if (!this.level.isClientSide) return;
            if (ServerStalker.getCameraEntity(player) == null) return;
            if (!this.level.getChunkSource().hasChunk(this.chunkPosition.x, this.chunkPosition.z)) {
                ci.cancel();
            }
        }
    }
}
