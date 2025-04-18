package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.api.ICamera;
import com.mafuyu404.diligentstalker.event.CameraEvent;
import com.mafuyu404.diligentstalker.event.TestEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements ICamera {
    @Shadow protected abstract void setPosition(double p_90585_, double p_90586_, double p_90587_);

//    @Inject(method = "tick", at = @At("RETURN"))
//    private void onCameraTick(CallbackInfo ci) {
//        Player player = Minecraft.getInstance().player;
//        if (player == null) return;
//        if (CameraEvent.isCameraMode) {
//            System.out.print(CameraEvent.cameraX+""+CameraEvent.cameraY+CameraEvent.cameraZ+"\n");
//            this.setPosition(CameraEvent.cameraX, CameraEvent.cameraY, CameraEvent.cameraZ);
//        }
//    }

//    public void setCameraPosition(double x, double y, double z) {
//        this.setPosition(x, y, z);
////        System.out.print(x+"-"+y+"-"+z+"\n");
//    }
//
//    @Inject(method = "setup", at = @At("HEAD"), cancellable = true)
//    private void onSetup(BlockGetter p_90576_, Entity p_90577_, boolean p_90578_, boolean p_90579_, float p_90580_, CallbackInfo ci) {
    @Shadow private float xRot;

    @Shadow private float yRot;

    @Shadow private Vec3 position;

    ////        System.out.print("refresh");
//        if (CameraEvent.isCameraMode) {
//            this.setPosition(CameraEvent.cameraX, CameraEvent.cameraY, CameraEvent.cameraZ);
//            ci.cancel();
//        }
//    }
    @ModifyVariable(method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), argsOnly = true)
    private Vec3 modifyPosition(Vec3 pos1) {
        if (TestEvent.targetEntity == null) return pos1;
        Vec3 pos0 = this.position;
        double x = lerp(pos0.x, pos1.x);
        double y = lerp(pos0.y, pos1.y);
        double z = lerp(pos0.z, pos1.z);
        return new Vec3(x, y, z);
    }
    @Inject(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"))
    private void modifyRotate(float p_90573_, float p_90574_, CallbackInfo ci) {
        if (TestEvent.targetEntity == null) return;
//        System.out.print(Minecraft.getInstance().player.getXRot()+"\n");
//        this.xRot = Minecraft.getInstance().player.getXRot();
//        this.yRot = Minecraft.getInstance().player.getYRot();
        this.xRot = TestEvent.xRot;
        this.yRot = TestEvent.yRot;
    }

    private static double lerp(double current, double target) {
        return current + (target - current) * 0.3;
    }
}
