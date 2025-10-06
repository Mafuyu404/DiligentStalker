package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.chunk.DiligentChunkManager;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void diligent$ensureTickWhenForced(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;
        if (DiligentChunkManager.hasForcedChunks(self)) {
            self.resetEmptyTime();
        }
    }
}