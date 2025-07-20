package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
        if (!Stalker.hasInstanceOf(Minecraft.getInstance().player)) return;
        this.xRot = StalkerControl.xRot;
        this.yRot = StalkerControl.yRot;
    }
}
