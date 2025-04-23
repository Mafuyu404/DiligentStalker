package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.CameraEntityManage;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRenderDispatcher.RenderChunk.class)
public class ChunkRenderDispatcherRenderChunkMixin {
    @Inject(method = "getDistToPlayerSqr", at = @At("RETURN"))
    private void modifyCameraPosition(CallbackInfoReturnable<Double> cir) {
        if (CameraEntityManage.targetEntity != null) {
//            System.out.print(cir.getReturnValue()+"\n");
        }
    }
}
