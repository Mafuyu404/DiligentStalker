package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientLevel.class)
public abstract class ClientLevelMixin {
    @Shadow protected abstract LevelEntityGetter<Entity> getEntities();

    @Inject(method = "removeEntity", at = @At("HEAD"))
    private void wwaaa(int p_171643_, Entity.RemovalReason p_171644_, CallbackInfo ci) {
        Entity entity = this.getEntities().get(p_171643_);
        if (entity instanceof DroneStalkerEntity droneStalker) {
//            System.out.print(droneStalker+"\n");
        }
    }
}
