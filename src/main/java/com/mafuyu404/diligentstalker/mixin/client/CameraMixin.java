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

    @Inject(method = "setRotation", at = @At("RETURN"))
    private void modifyRotate(float p_90573_, float p_90574_, CallbackInfo ci) {
        Entity stalker = ClientStalkerUtil.getLocalStalker();
        if (stalker != null) {
            String cameraState = ControllableUtils.getCameraState(stalker);
            if (cameraState.equals("free")) {
                // 自由模式：使用固定的旋转角度
                this.xRot = StalkerControl.fixedXRot;
                this.yRot = StalkerControl.fixedYRot;
            } else if (cameraState.equals("follow")) {
                // 跟随模式：跟随实体的旋转
                this.xRot = stalker.getXRot();
                this.yRot = stalker.getYRot();
            }
            // 控制模式：不设置相机旋转，让鼠标输入正常工作
        }
    }
}
