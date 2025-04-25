package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.CameraEntityManage;
import com.mafuyu404.diligentstalker.event.ServerStalker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public class TrackedEntityMixin {
    @Shadow @Final private Entity entity;

//    @ModifyVariable(method = "updatePlayer", at = @At("STORE"), ordinal = 1)
//    private double wwaaas(double value) {
//        if (this.entity instanceof DroneStalkerEntity droneStalker) {
//            if (droneStalker.underControlling()) {
//                return 1;
//            }
//        }
//        return value;
//    }
    @Redirect(method = "updatePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;position()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 wwa(ServerPlayer player) {
        Entity stalker = ServerStalker.getCameraEntity(player);
        return Objects.requireNonNullElse(stalker, player).position();
    }
}
