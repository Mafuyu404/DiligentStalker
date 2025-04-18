package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.TestEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class)
public class EntityMixin {
    @Inject(method = "setXRot", at = @At("HEAD"), cancellable = true)
    private void redirectXRot(float xRot, CallbackInfo ci) {
        if (TestEvent.targetEntity == null) return;
        if (((Object) this) instanceof Player) {
            TestEvent.xRot += xRot - TestEvent.fixedXRot;
            ci.cancel();
        }
    }

    @Inject(method = "setYRot", at = @At("HEAD"), cancellable = true)
    private void redirectYRot(float yRot, CallbackInfo ci) {
        if (TestEvent.targetEntity == null) return;
        if (((Object) this) instanceof Player) {
            TestEvent.yRot += yRot - TestEvent.fixedYRot;
            ci.cancel();
        }
    }
}
