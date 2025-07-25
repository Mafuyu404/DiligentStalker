package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.event.StalkerManage;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(value = Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract Level level();

    @Shadow
    private Level level;

    @Shadow
    private ChunkPos chunkPosition;

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    protected UUID uuid;

    @Inject(method = "setXRot", at = @At("HEAD"), cancellable = true)
    private void redirectXRot(float xRot, CallbackInfo ci) {
        if (((Object) this) instanceof Player player) {
            if (!player.isLocalPlayer()) return;
            if (!Stalker.hasInstanceOf(player)) return;
            if (!StalkerControl.screen) {
                StalkerControl.xRot += xRot - StalkerControl.fixedXRot;
            }
            ci.cancel();
        }
    }

    @Inject(method = "setYRot", at = @At("HEAD"), cancellable = true)
    private void redirectYRot(float yRot, CallbackInfo ci) {
        if (((Object) this) instanceof Player player) {
            if (!player.isLocalPlayer()) return;
            if (!Stalker.hasInstanceOf(player)) return;
            if (!StalkerControl.screen) {
                StalkerControl.yRot += yRot - StalkerControl.fixedYRot;
            }
            ci.cancel();
        }
    }

    @Inject(method = "distanceToSqr(DDD)D", at = @At("HEAD"), cancellable = true)
    private void modifyDistance(double p_20276_, double p_20277_, double p_20278_, CallbackInfoReturnable<Double> cir) {
        if (((Object) this) instanceof Player player) {
            if (Stalker.hasInstanceOf(player)) {
                cir.setReturnValue(1d);
            }
        }
    }

    @Inject(method = "setPosRaw", at = @At("HEAD"), cancellable = true)
    private void avoidVoidFall(double p_20210_, double p_20211_, double p_20212_, CallbackInfo ci) {
        if (((Object) this) instanceof Player player) {
            if (!this.level.isClientSide) return;
            if (!Stalker.hasInstanceOf(player)) return;
            if (!this.level.getChunkSource().hasChunk(this.chunkPosition.x, this.chunkPosition.z)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "setPosRaw", at = @At("RETURN"))
    private void position(double p_20344_, double p_20345_, double p_20346_, CallbackInfo ci) {
        if (((Object) this) instanceof DroneStalkerEntity) {
            if (this.level.isClientSide) return;
//            if (!StalkerManage.DronePosition.containsKey(this.uuid)) return;
            String levelKey = this.level.dimension().toString();
            BlockPos blockPos = this.blockPosition();
            StalkerManage.DronePosition.put(this.uuid, new Map.Entry<>() {
                @Override
                public String getKey() {
                    return levelKey;
                }

                @Override
                public BlockPos getValue() {
                    return blockPos;
                }

                @Override
                public BlockPos setValue(BlockPos value) {
                    return null;
                }
            });
        }
    }
}
