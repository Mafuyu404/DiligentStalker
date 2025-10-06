package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.EntityDeathCallback;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void onStopUsingItem(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack stack = self.getUseItem();
        if (self instanceof Player player && !stack.isEmpty()) {
            if (!Stalker.hasInstanceOf(player)) {
                ClientStalkerUtil.cancelRemoteConnect();
            }
        }
    }
}
