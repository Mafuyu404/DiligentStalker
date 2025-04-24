package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.CameraEntityManage;
import com.mafuyu404.diligentstalker.event.ServerStalker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
public class EntityMixin {
    @Shadow @Final private Set<String> tags;

    @Shadow private int id;

    @Shadow protected UUID uuid;

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
    @Inject(method = "setRemoved", at = @At("HEAD"), cancellable = true)
    private void wwaaaa(Entity.RemovalReason p_146876_, CallbackInfo ci) {
        if (((Object) this) instanceof DroneStalkerEntity droneStalker) {
//            if (droneStalker.underControlling()) ci.cancel();
        }
    }
//    @Inject(method = "isAddedToWorld", at = @At("HEAD"), cancellable = true)
//    private void wwwaaaa(CallbackInfoReturnable<Boolean> cir) {
//        if (((Object) this) instanceof DroneStalkerEntity droneStalker) {
//            if (droneStalker.underControlling()) {
//                System.out.print("???\n");
//                cir.setReturnValue(true);
//            }
//        }
//    }
}
