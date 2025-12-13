package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.EntityDeathCallback;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "die", at = @At("TAIL"))
    private void diligentstalker$onDie(DamageSource source, CallbackInfo ci) {
        EntityDeathCallback.EVENT.invoker().onDeath((LivingEntity) (Object) this, source);
    }
}
