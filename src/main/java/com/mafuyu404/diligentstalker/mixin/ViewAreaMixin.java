package com.mafuyu404.diligentstalker.mixin;

import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ViewArea.class)
public class ViewAreaMixin {
    @Shadow protected int chunkGridSizeX;

    @Shadow protected int chunkGridSizeZ;

    @Inject(method = "createChunks", at = @At("HEAD"))
    private void waaa(ChunkRenderDispatcher p_110865_, CallbackInfo ci) {
        System.out.print(this.chunkGridSizeX+"/"+this.chunkGridSizeZ+"\n");
    }
}
