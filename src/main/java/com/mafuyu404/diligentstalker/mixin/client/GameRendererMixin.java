package com.mafuyu404.diligentstalker.mixin.client;

import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void avoidRenderHandItem(Camera camera, float f, Matrix4f matrix4f, CallbackInfo ci) {
        if (Stalker.hasInstanceOf(camera.getEntity())) {
            ci.cancel();
        }
    }

    @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void getNightVisionScale(LivingEntity entity, float p_109110_, CallbackInfoReturnable<Float> cir) {
        if (Stalker.hasInstanceOf(entity)) {
            cir.setReturnValue(1f);
        }
    }
}
