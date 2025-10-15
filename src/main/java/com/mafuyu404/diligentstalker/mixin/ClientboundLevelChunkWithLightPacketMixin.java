package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;

import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundLevelChunkWithLightPacket.class)
public class ClientboundLevelChunkWithLightPacketMixin {

    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("HEAD"), cancellable = true)
    private void interceptHandle(ClientGamePacketListener listener, CallbackInfo ci) {
        ClientboundLevelChunkWithLightPacket self = (ClientboundLevelChunkWithLightPacket)(Object)this;
        boolean intercepted = ClientStalkerUtil.handleChunkPacket(self);

        if (intercepted) {
            DiligentStalker.debug(ClientboundLevelChunkWithLightPacketMixin.class, "intercept chunk packet x={} z={} -> redirected to task",
                    self.getX(), self.getZ());
            ci.cancel();
        } else {
//            DiligentStalker.trace("[DS][client] pass through chunk packet x={} z={}",
//                    self.getX(), self.getZ());
        }
    }
}
