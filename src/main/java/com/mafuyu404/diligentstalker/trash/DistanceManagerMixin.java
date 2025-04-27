package com.mafuyu404.diligentstalker.trash;

import net.minecraft.core.SectionPos;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DistanceManager.class)
public class DistanceManagerMixin {
    @Inject(method = "removePlayer", at = @At("HEAD"), cancellable = true)
    private void saa(SectionPos p_140829_, ServerPlayer p_140830_, CallbackInfo ci) {
        if (p_140830_.getTags().contains("fake")) ci.cancel();
    }
}
