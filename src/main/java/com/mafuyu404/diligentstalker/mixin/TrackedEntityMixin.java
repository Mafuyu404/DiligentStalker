package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public class TrackedEntityMixin {
    @Shadow @Final private Entity entity;

    @ModifyVariable(method = "updatePlayer", at = @At("STORE"))
    private boolean wwaaas(boolean value) {
        if (this.entity instanceof DroneStalkerEntity droneStalker) {
            if (droneStalker.underControlling()) {
                return true;
            }
        }
        return value;
    }
}
