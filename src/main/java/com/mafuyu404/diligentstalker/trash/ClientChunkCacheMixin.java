package com.mafuyu404.diligentstalker.trash;

import net.minecraft.client.multiplayer.ClientChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
@Mixin(value = ClientChunkCache.class)
public class ClientChunkCacheMixin {
    @Inject(method = "updateViewCenter", at = @At("HEAD"), cancellable = true)
    private void wwa(int p_104460_, int p_104461_, CallbackInfo ci) {
//        System.out.print(p_104460_+"/"+p_104461_+"\n");
    }
}
