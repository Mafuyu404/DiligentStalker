package com.mafuyu404.diligentstalker.mixin;

import net.minecraft.client.multiplayer.ClientChunkCache;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientChunkCache.class, remap = false)
public class ClientChunkCacheMixin {
    @Redirect(method = "replaceWithPacketData", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void justShutDown(Logger instance, String string, Object o, Object b) {
//        instance.warn(string, o, b);
    }
}
