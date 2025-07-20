package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientboundLevelChunkWithLightPacket.class)
public class ClientboundLevelChunkWithLightPacketMixin {
    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("HEAD"), cancellable = true)
    private void www(ClientGamePacketListener packetListener, CallbackInfo ci) {
        boolean result = ClientStalkerUtil.handleChunkPacket((ClientboundLevelChunkWithLightPacket) (Object) this);
        if (result) ci.cancel();
    }
}
