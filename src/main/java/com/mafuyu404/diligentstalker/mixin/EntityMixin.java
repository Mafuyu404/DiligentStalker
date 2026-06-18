package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.event.StalkerManage;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(value = Entity.class)
public abstract class EntityMixin {
    @Shadow
    private Level level;

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

    @Inject(method = "setPosRaw", at = @At("HEAD"), cancellable = true)
    private void avoidVoidFall(double x, double y, double z, CallbackInfo ci) {
        if (((Object) this) instanceof Player player) {
            if (!this.level.isClientSide) return;
            if (!Stalker.hasInstanceOf(player)) return;
            int chunkX = ((int) Math.floor(x)) >> 4;
            int chunkZ = ((int) Math.floor(z)) >> 4;
            if (!this.level.getChunkSource().hasChunk(chunkX, chunkZ)) {
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
