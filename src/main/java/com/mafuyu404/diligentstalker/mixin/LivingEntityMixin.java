package com.mafuyu404.diligentstalker.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LivingEntity.class)
public class LivingEntityMixin {
//    @Inject(method = "aiStep", at = @At("RETURN"), cancellable = true)
//    private void noAI(CallbackInfo ci) {
//        if (CameraEntityManage.targetEntity != null || ServerStalker.entityId != -1) {
//            ci.cancel();
//        }
//    }
}
