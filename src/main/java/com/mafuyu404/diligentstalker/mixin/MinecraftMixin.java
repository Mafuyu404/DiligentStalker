package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.CameraEntityManage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "getCameraEntity", at = @At("HEAD"), cancellable = true)
    private void ddd(CallbackInfoReturnable<Entity> cir) {
        if (CameraEntityManage.isEnable()) {
            cir.setReturnValue(CameraEntityManage.targetEntity);
        }
    }
}
