package com.mafuyu404.diligentstalker.mixin.client;

import com.mafuyu404.diligentstalker.event.handler.StalkerControl;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import com.mafuyu404.diligentstalker.utils.ControllableUtils;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private float xRot;

    @Shadow
    private float yRot;

    @Shadow
    private Vec3 position;

    @Shadow
    @Final
    private Quaternionf rotation;

    @Shadow
    @Final
    private Vector3f forwards;

    @Shadow
    @Final
    private Vector3f up;

    @Shadow
    @Final
    private Vector3f left;

    private static final Vector3f FORWARDS = new Vector3f(0.0F, 0.0F, -1.0F);
    private static final Vector3f UP = new Vector3f(0.0F, 1.0F, 0.0F);
    private static final Vector3f LEFT = new Vector3f(-1.0F, 0.0F, 0.0F);

    @ModifyVariable(method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), argsOnly = true)
    private Vec3 modifyPosition(Vec3 pos1) {
        Player player = Minecraft.getInstance().player;
        if (Stalker.hasInstanceOf(player)) {
            Vec3 pos0 = this.position;
            double x = StalkerUtil.lerp(pos0.x, pos1.x);
            double y = StalkerUtil.lerp(pos0.y, pos1.y);
            double z = StalkerUtil.lerp(pos0.z, pos1.z);
            return new Vec3(x, y, z);
        } else {
            if (ClientStalkerUtil.getVisualCenter() != null) return ClientStalkerUtil.getVisualCenter().getCenter();
        }
        return pos1;
    }

    @Inject(method = "setRotation(FF)V", at = @At("RETURN"))
    private void modifyRotate2Param(float yRot, float xRot, CallbackInfo ci) {
        Entity stalker = ClientStalkerUtil.getLocalStalker();
        if (stalker != null) {
            if (!ControllableUtils.isCameraFollowing(stalker)) {
                updateCameraRotation(StalkerControl.yRot, StalkerControl.xRot, 0.0F);
            }
        }
    }

    @Inject(method = "setRotation(FFF)V", at = @At("RETURN"))
    private void modifyRotate3Param(float yRot, float xRot, float roll, CallbackInfo ci) {
        Entity stalker = ClientStalkerUtil.getLocalStalker();
        if (stalker != null) {
            if (!ControllableUtils.isCameraFollowing(stalker)) {
                updateCameraRotation(StalkerControl.yRot, StalkerControl.xRot, 0.0F);
            }
        }
    }

    private void updateCameraRotation(float yRot, float xRot, float roll) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.rotation.rotationYXZ((float)Math.PI - yRot * ((float)Math.PI / 180F), -xRot * ((float)Math.PI / 180F), -roll * ((float)Math.PI / 180F));
        FORWARDS.rotate(this.rotation, this.forwards);
        UP.rotate(this.rotation, this.up);
        LEFT.rotate(this.rotation, this.left);
    }
}
